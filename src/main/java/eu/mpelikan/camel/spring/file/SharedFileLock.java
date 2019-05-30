package eu.mpelikan.camel.spring.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

@Component
public class SharedFileLock {


    private static final Logger LOG = LoggerFactory.getLogger(SharedFileLock.class.getName());
    private RandomAccessFile lockFile;
    private File lockPath;
    private FileLock lock;

    public SharedFileLock(String folder) {
        try {
            String lock = folder;
            File base = new File(lock);
            if (!base.exists()) {
                LOG.info("Creating new base path");
                base.mkdirs();
            } else {
                LOG.info("Base exists:" + base.exists() + " and process can write:" + base.canWrite());
            }
            lockPath = new File(base, "lock");
            lockFile = new RandomAccessFile(lockPath, "rw");
            lock();
        } catch (OverlappingFileLockException e) {
            // lock file locked by another process, do nothing
        } catch (IOException ioe){
            throw new RuntimeException("SharedFileLocker bean cannot start, make sure the lock file can be accessed and written by the user starting Karaf : " + ioe.getMessage(), ioe);
        } catch (Exception e){
            throw new RuntimeException("Could not create file lock: " + e.getMessage(), e);
        }
    }

    public synchronized boolean lock() throws Exception {
        LOG.info("Trying to lock " + lockPath.getPath());
        if (lock == null) {
            lock = lockFile.getChannel().tryLock();
        }
        if (lock != null) {
            LOG.info("Lock acquired");
        } else {
            LOG.info("Obtaining lock failed");
        }
        return lock != null;
    }

    /* if lock is not obtained, try to lock a file*/
    public synchronized boolean hasLock() throws Exception {
        if (!isAlive()) {
            try {
                lock();
            } catch (OverlappingFileLockException e) {
                // file locked by another process - we could simply ignore this exception
            } catch (IOException e) {
                LOG.error(e.getLocalizedMessage());
            }
        }
        return isAlive();
    }

    public synchronized void release() throws Exception {
        if (lock != null && lock.isValid()) {
            LOG.info("Releasing lock " + lockPath.getPath());
            lock.release();
            lock.channel().close();
        }
        lock = null;
    }

    public synchronized boolean isAlive() throws Exception {
        return lock != null && lock.isValid() && lockPath.exists();
    }

    @PreDestroy
    public void destroy() {
        if (lock != null && lock.isValid()) {
            LOG.info("Releasing lock " + lockPath.getPath());
            try {
                lock.release();
                lock.channel().close();
            } catch (IOException e){
                LOG.warn("Could not release lock on bean destroy");
            }
        }
        lock = null;
    }
}
