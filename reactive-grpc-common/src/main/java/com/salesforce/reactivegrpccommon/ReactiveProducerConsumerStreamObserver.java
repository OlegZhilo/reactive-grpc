/*
 *  Copyright (c) 2017, salesforce.com, inc.
 *  All rights reserved.
 *  Licensed under the BSD 3-Clause license.
 *  For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.reactivegrpccommon;

import com.google.common.base.Preconditions;
import io.grpc.stub.ClientCallStreamObserver;
import org.reactivestreams.Publisher;

/**
 * ReactorProducerConsumerStreamObserver configures client-side manual flow control for when the client is both producing
 * and consuming streams of messages.
 *
 * @param <TRequest>
 * @param <TResponse>
 */
public abstract class ReactiveProducerConsumerStreamObserver<TRequest, TResponse> extends ReactiveConsumerStreamObserver<TRequest, TResponse> {
    private Publisher<TRequest> rxProducer;
    private ReactivePublisherBackpressureOnReadyHandler<TRequest> onReadyHandler;

    public ReactiveProducerConsumerStreamObserver(Publisher<TRequest> rxProducer) {
        this.rxProducer = rxProducer;
    }

    @Override
    public void beforeStart(ClientCallStreamObserver<TRequest> requestStream) {
        super.beforeStart(Preconditions.checkNotNull(requestStream));
        onReadyHandler = new ReactivePublisherBackpressureOnReadyHandler<>(requestStream);
    }

    public void rxSubscribe() {
        rxProducer.subscribe(onReadyHandler);
    }

    public void cancel() {
        onReadyHandler.cancel();
    }
}
