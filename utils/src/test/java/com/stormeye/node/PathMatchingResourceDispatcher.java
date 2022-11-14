package com.stormeye.node;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

public class PathMatchingResourceDispatcher extends Dispatcher {

    /** The resource to serve for the path that matches the pathMatcher */
    private final String rcpResponseBody;
    /** The matcher for the URL request path */
    private final Matcher<String> pathMatcher;

    public PathMatchingResourceDispatcher(final String rcpResponseBody, final Matcher<String> pathMatcher) {
        this.rcpResponseBody = rcpResponseBody;
        this.pathMatcher = pathMatcher;
    }

    @NotNull
    @Override
    public MockResponse dispatch(@NotNull final RecordedRequest recordedRequest) {

        if (pathMatcher.matches(recordedRequest.getPath())) {

            return new MockResponse().setResponseCode(200)
                    .addHeader("Content-Type", "application/json")
                    .setBody(rcpResponseBody);

        } else {
            // return not found if no match
            return new MockResponse().setResponseCode(404);
        }
    }
}
