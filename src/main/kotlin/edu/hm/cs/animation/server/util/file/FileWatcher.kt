/*
 * Copyright (c) Munich University of Applied Sciences - https://hm.edu/
 * Licensed under GNU General Public License 3 (See LICENSE.md in the repositories root)
 */

package edu.hm.cs.animation.server.util.file

import org.slf4j.LoggerFactory

import java.io.IOException
import java.nio.file.*

class FileWatcher {

    private var thread: Thread? = null
    private var watchService: WatchService? = null

    @Throws(IOException::class)
    fun start(file: Path, callback: Runnable) {
        watchService = FileSystems.getDefault().newWatchService()
        val parent = file.parent
        parent.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE)
        log.info("Going to watch $file")

        thread = Thread {
            while (true) {
                var wk: WatchKey? = null
                try {
                    wk = watchService!!.take()
                    Thread.sleep(500) // give a chance for duplicate events to pile up
                    for (event in wk!!.pollEvents()) {
                        val changed = parent.resolve(event.context() as Path)
                        if (Files.exists(changed) && Files.isSameFile(changed, file)) {
                            log.info("File change event: $changed")
                            callback.run()
                            break
                        }
                    }
                } catch (e: InterruptedException) {
                    log.info("Ending my watch")
                    Thread.currentThread().interrupt()
                    break
                } catch (e: Exception) {
                    log.error("Error while reloading cert", e)
                } finally {
                    wk?.reset()
                }
            }
        }
        thread!!.start()
    }

    fun stop() {
        thread!!.interrupt()
        try {
            watchService!!.close()
        } catch (e: IOException) {
            log.info("Error closing watch service", e)
        }

    }

    companion object {
        private val log = LoggerFactory.getLogger(FileWatcher::class.java)

        /**
         * Starts watching a file and the given path and calls the callback when it is changed.
         * A shutdown hook is registered to stop watching. To control this yourself, create an
         * instance and use the start/stop methods.
         */
        @Throws(IOException::class)
        fun onFileChange(file: Path, callback: Runnable) {
            val fileWatcher = FileWatcher()
            fileWatcher.start(file, callback)
            Runtime.getRuntime().addShutdownHook(Thread(Runnable { fileWatcher.stop() }))
        }
    }

}
