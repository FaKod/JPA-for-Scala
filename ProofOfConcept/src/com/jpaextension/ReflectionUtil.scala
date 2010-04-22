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

package com.jpaextension

/**
 * class for reflecting Scala or Java classes
 */

object ReflectionUtil {
  /**
   * class of scalaObject to check if this is a scala object
   */
  private val scalaObject = Class.forName("scala.ScalaObject")

  /**
   * get Field content
   */
  def getField[T](o:AnyRef, fieldName:String):T = {
    val f = o.getClass.getField(fieldName)
    f.get(o).asInstanceOf[T]
  }

  /**
   *  set Field content
   */
  def setField(o:AnyRef, value:AnyRef, fieldName:String):Unit = {
    val f = o.getClass.getField(fieldName)
    f.set(o, value)
  }

  /**
   * get Scala Field content
   */
  def getFilterPropertyField[T](o:AnyRef, fieldName:String, array:Array[Class[_]] = Array[Class[_]]()):T = {
    if(scalaObject.isAssignableFrom(o.getClass)) {
      val method = o.getClass.getMethod(fieldName, array:_*)
      method.invoke(o, array:_*).asInstanceOf[T]
    }
    else
      getField(o, fieldName)
  }
}