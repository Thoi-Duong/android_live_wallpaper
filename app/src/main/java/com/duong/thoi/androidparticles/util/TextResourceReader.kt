package com.duong.thoi.androidparticles.util
import java.io.InputStreamReader
import android.content.Context
import android.content.res.Resources
import java.io.BufferedReader
import java.io.IOException

/**
 * Created by thoiduong on 11/12/18.
 */

object TextResourceReader {
    fun readTextFileFromResource(context: Context, resourceId: Int): String {
        val body = StringBuilder()

        try {
            val inputStream = context.resources.openRawResource(resourceId)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var nextLine: String?
            nextLine = bufferedReader.readLine()
            while (nextLine != null) {
                body.append(nextLine)
                body.append('\n')
                nextLine = bufferedReader.readLine()
            }

        } catch (e: IOException) {
            throw RuntimeException("Could not open resource: " + resourceId, e);
        }
        catch (nfe: Resources.NotFoundException) {
            throw RuntimeException("Resource not found: " + resourceId, nfe);
        }
        return body.toString()
    }
}