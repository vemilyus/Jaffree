/*
 *    Copyright  2020 Alex Katlein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.kokorin.jaffree.process;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

class ProcessFutureImpl<V> implements ProcessFuture<V> {
    private final CompletableFuture<V> delegate;
    private final ProcessAccess processAccess;

    ProcessFutureImpl(CompletableFuture<V> delegate, ProcessAccess processAccess) {
        Objects.requireNonNull(delegate, "delegate must not be null");

        this.delegate = delegate;
        this.processAccess = processAccess;
    }

    @Override
    public ProcessAccess getProcessAccess() {
        return processAccess;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (mayInterruptIfRunning) {
            getProcessAccess().stopForcefully();
        } else {
            getProcessAccess().stopGracefully();
        }

        return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }

    @Override
    public <U> ProcessFuture<U> thenApply(Function<? super V, ? extends U> fn) {
        return new ProcessFutureImpl<>(delegate.thenApply(fn), getProcessAccess());
    }

    @Override
    public <U> ProcessFuture<U> thenApplyAsync(Function<? super V, ? extends U> fn) {
        return new ProcessFutureImpl<>(delegate.thenApplyAsync(fn), getProcessAccess());
    }

    @Override
    public <U> ProcessFuture<U> thenApplyAsync(Function<? super V, ? extends U> fn, Executor executor) {
        return new ProcessFutureImpl<>(delegate.thenApplyAsync(fn, executor), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> thenAccept(Consumer<? super V> action) {
        return new ProcessFutureImpl<>(delegate.thenAccept(action), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> thenAcceptAsync(Consumer<? super V> action) {
        return new ProcessFutureImpl<>(delegate.thenAcceptAsync(action), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> thenAcceptAsync(Consumer<? super V> action, Executor executor) {
        return new ProcessFutureImpl<>(delegate.thenAcceptAsync(action, executor), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> thenRun(Runnable action) {
        return new ProcessFutureImpl<>(delegate.thenRun(action), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> thenRunAsync(Runnable action) {
        return new ProcessFutureImpl<>(delegate.thenRunAsync(action), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> thenRunAsync(Runnable action, Executor executor) {
        return new ProcessFutureImpl<>(delegate.thenRunAsync(action, executor), getProcessAccess());
    }

    @Override
    public <U, V1> ProcessFuture<V1> thenCombine(CompletionStage<? extends U> other, BiFunction<? super V, ? super U, ? extends V1> fn) {
        return new ProcessFutureImpl<>(delegate.thenCombine(other, fn), getProcessAccess());
    }

    @Override
    public <U, V1> ProcessFuture<V1> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super V, ? super U, ? extends V1> fn) {
        return new ProcessFutureImpl<>(delegate.thenCombineAsync(other, fn), getProcessAccess());
    }

    @Override
    public <U, V1> ProcessFuture<V1> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super V, ? super U, ? extends V1> fn, Executor executor) {
        return new ProcessFutureImpl<>(delegate.thenCombineAsync(other, fn, executor), getProcessAccess());
    }

    @Override
    public <U> ProcessFuture<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super V, ? super U> action) {
        return new ProcessFutureImpl<>(delegate.thenAcceptBoth(other, action), getProcessAccess());
    }

    @Override
    public <U> ProcessFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super V, ? super U> action) {
        return new ProcessFutureImpl<>(delegate.thenAcceptBothAsync(other, action), getProcessAccess());
    }

    @Override
    public <U> ProcessFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super V, ? super U> action, Executor executor) {
        return new ProcessFutureImpl<>(delegate.thenAcceptBothAsync(other, action, executor), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
        return new ProcessFutureImpl<>(delegate.runAfterBoth(other, action), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
        return new ProcessFutureImpl<>(delegate.runAfterBothAsync(other, action), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return new ProcessFutureImpl<>(delegate.runAfterBothAsync(other, action, executor), getProcessAccess());
    }

    @Override
    public <U> ProcessFuture<U> applyToEither(CompletionStage<? extends V> other, Function<? super V, U> fn) {
        return new ProcessFutureImpl<>(delegate.applyToEither(other, fn), getProcessAccess());
    }

    @Override
    public <U> ProcessFuture<U> applyToEitherAsync(CompletionStage<? extends V> other, Function<? super V, U> fn) {
        return new ProcessFutureImpl<>(delegate.applyToEitherAsync(other, fn), getProcessAccess());
    }

    @Override
    public <U> ProcessFuture<U> applyToEitherAsync(CompletionStage<? extends V> other, Function<? super V, U> fn, Executor executor) {
        return new ProcessFutureImpl<>(delegate.applyToEitherAsync(other, fn, executor), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> acceptEither(CompletionStage<? extends V> other, Consumer<? super V> action) {
        return new ProcessFutureImpl<>(delegate.acceptEither(other, action), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> acceptEitherAsync(CompletionStage<? extends V> other, Consumer<? super V> action) {
        return new ProcessFutureImpl<>(delegate.acceptEitherAsync(other, action), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> acceptEitherAsync(CompletionStage<? extends V> other, Consumer<? super V> action, Executor executor) {
        return new ProcessFutureImpl<>(delegate.acceptEitherAsync(other, action, executor), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
        return new ProcessFutureImpl<>(delegate.runAfterEither(other, action), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
        return new ProcessFutureImpl<>(delegate.runAfterEitherAsync(other, action), getProcessAccess());
    }

    @Override
    public ProcessFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return new ProcessFutureImpl<>(delegate.runAfterEitherAsync(other, action, executor), getProcessAccess());
    }

    @Override
    public <U> ProcessFuture<U> thenCompose(Function<? super V, ? extends CompletionStage<U>> fn) {
        return new ProcessFutureImpl<>(delegate.thenCompose(fn), getProcessAccess());
    }

    @Override
    public <U> ProcessFuture<U> thenComposeAsync(Function<? super V, ? extends CompletionStage<U>> fn) {
        return new ProcessFutureImpl<>(delegate.thenComposeAsync(fn), getProcessAccess());
    }

    @Override
    public <U> ProcessFuture<U> thenComposeAsync(Function<? super V, ? extends CompletionStage<U>> fn, Executor executor) {
        return new ProcessFutureImpl<>(delegate.thenComposeAsync(fn, executor), getProcessAccess());
    }

    @Override
    public <U> ProcessFuture<U> handle(BiFunction<? super V, Throwable, ? extends U> fn) {
        return new ProcessFutureImpl<>(delegate.handle(fn), getProcessAccess());
    }

    @Override
    public <U> ProcessFuture<U> handleAsync(BiFunction<? super V, Throwable, ? extends U> fn) {
        return new ProcessFutureImpl<>(delegate.handleAsync(fn), getProcessAccess());
    }

    @Override
    public <U> ProcessFuture<U> handleAsync(BiFunction<? super V, Throwable, ? extends U> fn, Executor executor) {
        return new ProcessFutureImpl<>(delegate.handleAsync(fn, executor), getProcessAccess());
    }

    @Override
    public ProcessFuture<V> whenComplete(BiConsumer<? super V, ? super Throwable> action) {
        return new ProcessFutureImpl<>(delegate.whenComplete(action), getProcessAccess());
    }

    @Override
    public ProcessFuture<V> whenCompleteAsync(BiConsumer<? super V, ? super Throwable> action) {
        return new ProcessFutureImpl<>(delegate.whenCompleteAsync(action), getProcessAccess());
    }

    @Override
    public ProcessFuture<V> whenCompleteAsync(BiConsumer<? super V, ? super Throwable> action, Executor executor) {
        return new ProcessFutureImpl<>(delegate.whenCompleteAsync(action, executor), getProcessAccess());
    }

    @Override
    public ProcessFuture<V> exceptionally(Function<Throwable, ? extends V> fn) {
        return new ProcessFutureImpl<>(delegate.exceptionally(fn), getProcessAccess());
    }

    @Override
    public CompletableFuture<V> toCompletableFuture() {
        return delegate.toCompletableFuture();
    }
}
