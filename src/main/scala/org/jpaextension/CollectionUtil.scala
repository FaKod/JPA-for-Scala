/*
 * Copyright Christopher Schmidt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jpaextension

import java.{util => ju}
import scala.collection.{JavaConversions => conv}
import collection.{mutable => cm}

/**
 * Object to simplify usage of Java and Scala "Collections"
 * for eg. one to many relations
 * @author Christopher Schmidt
 */

object CollectionUtil {
  /**
   * type of the used Scala Collection
   */
  type SC[T] = cm.Buffer[T]

  /**
   * create new instance of the Java Collection
   * List will be implicitly converted to Buffer by JavaConversion
   * and vice versa
   */
  def :+[T]: ju.List[T] = new ju.ArrayList[T]

  /**
   * just to limit the implicit conversions and the
   * required imports
   */
  implicit def asList[A](b: cm.Buffer[A]): ju.List[A] = conv.asList[A](b)

  implicit def asBuffer[A](l: ju.List[A]): cm.Buffer[A] = conv.asBuffer[A](l)
}