package com.android.volley;

public interface IRequestQueue {
	<T> void finish(Request<T> request);
}
