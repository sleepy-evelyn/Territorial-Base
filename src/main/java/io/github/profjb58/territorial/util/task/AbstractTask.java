package io.github.profjb58.territorial.util.task;

import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public abstract class AbstractTask implements TaskInfo {

    protected final Identifier taskId;
    protected final Runnable taskRunnable;
    @Nullable
    protected final Runnable cancelRunnable;

    /**
     * Abstract task foundation class. Used primarily to allow storing and retrieving different
     * types of tasks from a single place
     *
     * @param taskId Unique task ID, used for storing and retrieval
     * @param taskRunnable Task to run on completion
     * @param cancelRunnable Task to run in the case of a soft fail
     */
    public AbstractTask(Identifier taskId, Runnable taskRunnable, @Nullable Runnable cancelRunnable) {
        this.taskId = taskId;
        this.taskRunnable = taskRunnable;
        this.cancelRunnable = cancelRunnable;
    }

    /**
     * Cancel the existing running task if one exists
     *
     * @param failHard A hard fail interrupts the task and doesn't trigger the cancel runnable task
     * @return Whether the task was cancelled successfully
     */
    public abstract boolean cancel(boolean failHard);
}
