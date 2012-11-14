/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 */
package org.jboss.elasticsearch.river.sysinfo.local;

import java.io.IOException;

import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestResponse;
import org.elasticsearch.rest.RestStatus;

/**
 * Implementation of {@link RestChannel} used for {@link SourceClientLocal}.
 * 
 * @author Vlastimil Elias (velias at redhat dot com)
 */
public class LocalRestChannel implements RestChannel {

  protected long TIMEOUT = 60 * 1000;

  protected RestResponse response;

  @Override
  public void sendResponse(RestResponse response) {
    this.response = response;
  }

  /**
   * Wait until response is set or timeout, and then return response content or throw exception in case of error.
   * 
   * @return content of response in success case.
   * @throws IOException in case of error response.
   * @throws InterruptedException
   */
  public String getResponseContent() throws IOException, InterruptedException {
    long start = System.currentTimeMillis();
    while (response == null) {
      if ((System.currentTimeMillis() - start) > TIMEOUT)
        throw new IOException("Request timmed out after " + TIMEOUT + "ms");
      Thread.sleep(50);
    }
    if (response.status() != RestStatus.OK) {
      throw new IOException("response status is " + response.status() + " and content " + response.content());
    }
    return (new String(response.content(), "UTF-8")).trim();
  }

}
