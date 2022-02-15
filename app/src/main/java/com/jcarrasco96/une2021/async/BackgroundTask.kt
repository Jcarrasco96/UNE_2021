package com.jcarrasco96.une2021.async

import android.app.Activity

object BackgroundTask {

    fun execute(activity: Activity, processTask: ProcessTask) {
        Thread {
            processTask.doInBackground()
            activity.runOnUiThread { processTask.onPostExecute() }
        }.start()
    }

    interface ProcessTask {

        fun onPostExecute()

        fun doInBackground()

    }

}

class Test {

    fun exe(activity: Activity, processTask: BackgroundTask.ProcessTask) {

        BackgroundTask.execute(activity, processTask)

    }

}