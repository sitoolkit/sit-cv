/*
 * Copyright 2013 Monocrea Inc.
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

package io.sitoolkit.cv.core.infra.watcher;

import java.util.Collection;

/**
 * 繰り返し生成を行うクラスが実装するインターフェースです。
 * 
 * @author yuichi.kuwahara
 */
@FunctionalInterface
public interface ContinuousGeneratable {

    /**
     * 繰り返し生成のイベントが検知されたら呼び出されるメソッドです。 実装クラスは生成処理を実装します。
     * 
     * @param inputSources
     *            イベントの元となった入力ソース
     */
    void regenerate(Collection<String> inputSources);
}
