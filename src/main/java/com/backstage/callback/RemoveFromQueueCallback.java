package com.backstage.callback;

import org.springframework.web.context.request.async.DeferredResult;
import java.util.List;

/*
 * 用於移除佇列內資訊，搭配DeferredResult使用。
 * 
 */
public class RemoveFromQueueCallback implements Runnable {

    private final DeferredResult<String> dr;
    private final List<DeferredResult<String>> queue;

    public RemoveFromQueueCallback(DeferredResult<String> dr, List<DeferredResult<String>> queue) {
        this.dr = dr;
        this.queue = queue;
    }

    @Override
    public void run() {
        queue.remove(dr);
    }
}