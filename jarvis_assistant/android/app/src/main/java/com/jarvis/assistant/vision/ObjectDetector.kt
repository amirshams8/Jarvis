package com.jarvis.assistant.vision

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ObjectDetector(private val context: Context) {
    
    private var interpreter: Interpreter? = null
    private val labels = mutableListOf<String>()
    
    init {
        try {
            // Load TFLite model
            val modelFile = FileUtil.loadMappedFile(context, "detect.tflite")
            interpreter = Interpreter(modelFile)
            
            // Load labels
            labels.addAll(FileUtil.loadLabels(context, "labelmap.txt"))
            
            Logger.log("TFLite Object Detector initialized")
        } catch (e: Exception) {
            Logger.log("Failed to load TFLite model: ${e.message}")
        }
    }
    
    data class Detection(
        val label: String,
        val confidence: Float,
        val bbox: RectF
    )
    
    fun detect(bitmap: Bitmap): List<Detection> {
        interpreter ?: return emptyList()
        
        try {
            // Preprocess image
            val inputBuffer = preprocessImage(bitmap)
            
            // Prepare output buffers
            val outputLocations = Array(1) { Array(10) { FloatArray(4) } }
            val outputClasses = Array(1) { FloatArray(10) }
            val outputScores = Array(1) { FloatArray(10) }
            val numDetections = FloatArray(1)
            
            // Run inference
            val outputs = mapOf(
                0 to outputLocations,
                1 to outputClasses,
                2 to outputScores,
                3 to numDetections
            )
            
            interpreter?.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputs)
            
            // Parse results
            val detections = mutableListOf<Detection>()
            val count = numDetections[0].toInt().coerceAtMost(10)
            
            for (i in 0 until count) {
                val score = outputScores[0][i]
                if (score > 0.5f) { // Confidence threshold
                    val classIndex = outputClasses[0][i].toInt()
                    val label = if (classIndex < labels.size) labels[classIndex] else "Unknown"
                    
                    val bbox = RectF(
                        outputLocations[0][i][1] * bitmap.width,
                        outputLocations[0][i][0] * bitmap.height,
                        outputLocations[0][i][3] * bitmap.width,
                        outputLocations[0][i][2] * bitmap.height
                    )
                    
                    detections.add(Detection(label, score, bbox))
                }
            }
            
            return detections
        } catch (e: Exception) {
            Logger.log("Detection error: ${e.message}")
            return emptyList()
        }
    }
    
    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val inputSize = 300 // MobileNet SSD input size
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        
        val buffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        buffer.order(ByteOrder.nativeOrder())
        
        val pixels = IntArray(inputSize * inputSize)
        scaledBitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)
        
        for (pixel in pixels) {
            buffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f) // R
            buffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)  // G
            buffer.putFloat((pixel and 0xFF) / 255.0f)          // B
        }
        
        return buffer
    }
    
    fun close() {
        interpreter?.close()
    }
}
