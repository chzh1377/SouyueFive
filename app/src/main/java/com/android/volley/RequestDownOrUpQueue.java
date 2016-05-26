/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.zhongsou.souyue.net.volley.CSYRequest;

import java.util.concurrent.PriorityBlockingQueue;


/**
 * A request dispatch queue with a thread pool of dispatchers.
 *
 * Calling {@link #add(Request)} will enqueue the given Request for dispatch,
 * resolving from either cache or network on a worker thread, and then delivering
 * a parsed response on the main thread.
 */
public class RequestDownOrUpQueue extends RequestQueue{

    /** Callback interface for completed requests. */
    public static interface RequestFinishedListener<T> {
        /** Called when a request has finished processing. */
        public void onRequestFinished(Request<T> request);
    }
    private static final int DEFAULT_DOWNLOAD_THREAD_POOL_SIZE = 3;
    /** The queue of requests that are actually download file*/
    private final PriorityBlockingQueue<Request<?>> mDownloadQueue =
            new PriorityBlockingQueue<Request<?>>();

    private NetworkDispatcher[] mDownloadDispatchers;

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param network A Network interface for performing HTTP requests
     * @param threadPoolSize Number of network dispatcher threads to create
     * @param delivery A ResponseDelivery interface for posting responses and errors
     */
    public RequestDownOrUpQueue(Network network, int threadPoolSize,
                                ResponseDelivery delivery) {
        super(null,network,threadPoolSize,delivery);
        mDownloadDispatchers = new NetworkDispatcher[threadPoolSize];
    }

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param network A Network interface for performing HTTP requests
     * @param threadPoolSize Number of network dispatcher threads to create
     */
    public RequestDownOrUpQueue(Network network, int threadPoolSize) {
        this(network, threadPoolSize,
                new ExecutorDelivery(new Handler(Looper.getMainLooper())));
    }

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param network A Network interface for performing HTTP requests
     */
    public RequestDownOrUpQueue(Network network) {
        this(network, DEFAULT_DOWNLOAD_THREAD_POOL_SIZE);
    }

    /**
     * Starts the dispatchers in this queue.
     */
    public void start() {
        stop();  // Make sure any currently running dispatchers are stopped.

        // Create network dispatchers (and corresponding threads) up to the pool size.
        for (int i = 0; i < mDownloadDispatchers.length; i++) {
            NetworkDispatcher networkDispatcher = new NetworkDispatcher(mDownloadQueue, mNetwork,
                    null, mDelivery);
            mDownloadDispatchers[i] = networkDispatcher;
            networkDispatcher.start();
        }
    }

    /**
     * Stops the cache and network dispatchers.
     */
    public void stop() {
        for (int i = 0; i < mDownloadDispatchers.length; i++) {
            if (mDownloadDispatchers[i] != null) {
                mDownloadDispatchers[i].quit();
            }
        }
    }

    @Override
    public void cancelAll(RequestFilter filter) {
        throw new IllegalStateException("不支持的操作！！！");
    }

    @Override
    public void cancelAll(Object tag) {
        cancelAllWithId((Integer)tag);
    }

    public void cancelAllWithId(int tag) {
        synchronized (mCurrentRequests) {
            for (Request<?> request : mCurrentRequests) {
                DownOrUpRequest request1 = (DownOrUpRequest) request;
                request1.cancelDownload(tag);
                if (request.getTag()!=null&&request.getTag().equals(tag)){
                    request.cancel();
                }
            }
        }
    }

    /**
     * Adds a Request to the dispatch queue.
     * @param request The request to service
     * @return The passed-in request
     */
    public Request add(DownOrUpRequest request) {
        // Tag the request as belonging to this queue and add it to the set of current requests.
        request.setRequestQueue(this);
        synchronized (mCurrentRequests) {
            for (Request req:mCurrentRequests) {
                if (req instanceof DownOrUpRequest) {
                    if(((DownOrUpRequest) req).isInDownloadQueue(request.getUrl())){
                        request.addMarker("request exists add to queue!");
                        ((DownOrUpRequest) req).addDownloadRequest(request);
                        return request;
                    }
                }
            }
            mCurrentRequests.add(request);
        }

        request.setSequence(getSequenceNumber());
        request.addMarker("add-to-queue");
        mDownloadQueue.add(request);
        return request;
    }


    public boolean isRunning(String url){
        for (Request req:mCurrentRequests){
            if (req instanceof CSYRequest){
                if(req.getUrl().equals(url)){
                    return true;
                }
            }
        }
        for (Request req:mDownloadQueue){
            if (req instanceof CSYRequest){
                if(req.getUrl().equals(url)){
                    return true;
                }
            }
        }
        return false;
    }
}
