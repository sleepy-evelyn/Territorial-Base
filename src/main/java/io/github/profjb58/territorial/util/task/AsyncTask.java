package io.github.profjb58.territorial.util.task;

import io.github.profjb58.territorial.exception.ScheduleException;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AsyncTask extends AbstractTask {

    @Nullable
    protected Future<?> future;

    public AsyncTask(Identifier taskId, Runnable taskRunnable, @Nullable Runnable cancelRunnable) {
        super(taskId, taskRunnable, cancelRunnable);
    }

    public void submit(ExecutorService scheduler) {
        if(scheduler != null)
            future = scheduler.submit(taskRunnable);
    }

    @Override
    public boolean cancel(boolean failHard) {
        if(!failHard && cancelRunnable != null)
            cancelRunnable.run();
        if(future != null)
            return future.cancel(true);
        return false;
    }

    @Override
    public boolean isActive() throws ScheduleException {
        if(future != null)
            return !future.isDone();
        else
            throw new ScheduleException();
    }

    private void exception() {

    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
