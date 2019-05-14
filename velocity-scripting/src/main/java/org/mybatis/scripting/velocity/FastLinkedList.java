/**
 *    Copyright 2012-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.scripting.velocity;

import java.io.Serializable;

/**
 * Paranoiac small and fast forward only list
 */
public final class FastLinkedList<E extends Serializable> implements Serializable {

  private static final long serialVersionUID = 1L;

  private Node first;

  private Node last;

  public FastLinkedList() {
    this.last = this.first;
  }

  public boolean isEmpty() {
    return this.first == null;
  }

  public Node start() {
    return this.first;
  }

  public void add(E e) {
    final Node n = new Node(e);
    if (this.first == null) {
      this.first = n;
      this.last = this.first;
    } else {
      this.last.next = n;
      this.last = n;
    }
  }

  public final class Node {

    final E data;
    Node next;

    public Node(E newData) {
      this.data = newData;
    }

    public boolean hasNext() {
      return this.next != null;
    }

  }

}
