/*
 * Student : Robert Sanborn
 * Date : March 2, 2017
 * File : CS145LinkedList.java
 *
 * WWU: CS145 Winter 2017, Assignment 3
 *
 * CS145LinkedList.java -- A skeleton for a basic generic
 * linked list.
 *
 * Author: Chris Reedy (Chris.Reedy@wwu.edu).
 */


import java.util.Iterator;

public class CS145LinkedList<E> implements Iterable<E> {


   /* The ListNode class for this list. */
   private class ListNode {
      E data;
      ListNode next;
     
      /* Construct a ListNode containing data. */
      ListNode(E data) {
         this.data = data;
         next = null;
      }
      
      /* Construct a ListNode containing data, setting the
       * next. */
      ListNode(E data, ListNode next) {
         this.data = data;
         this.next = next;
      }
   }

   /* The first ListNode in the List. */
   private ListNode front;

   // The number of non-null objects inside the CS145LinkedList
   private int collectionSize = 0;
   
   /* Construct an empty list object. */
   public CS145LinkedList() {
      front = null;
   }

   /* Return the size (number of items) in this list. */
   public int size() {
      return this.collectionSize;
   }
   
   /* Return true if this CS145LinkedList has no items.
    * (This is the same as the size equal to zero.) Return
    * false if the size is greater than zero. */
   public boolean isEmpty() {
      return (this.collectionSize == 0);
   }
   
   /* Add the given element, value,  to the end of the list. */
   public void add(E value) {
      if (front == null) {
         front = new ListNode(value);
      } else {
         ListNode current = front;
         while (current.next != null) {
            current = current.next;
         }
         current.next = new ListNode(value);
      }
      // After element is added, increment the size of the collection
      this.collectionSize++;
   }
   
   /* Add the given element, value, to the list at the given index.
    * After this operation is complete, get(index) will return value.
    * This operation is only valid for 0 <= index <= size(). */
   public void add(int index, E value) {
      // If index is greater than size or negative then
      // IllegalArguments Exception is thrown
      if (this.size() < index || index < 0){
         throw new IllegalArgumentException();

         // Or, if the index is 0, then element is added to front of the
         // CS145LinkedList
      } else if (index == 0){
         ListNode newNode = new ListNode(value);
         newNode.next = this.front;
         this.front = newNode;

         // Otherwise the CS145LinkedList is iterated
      } else {

         ListNode newNode = new ListNode(value);
         ListNode current = this.front;

         // Iterate until current points to the node right before
         // the node at the desired index
         for (int i = 0; i < index - 1; i++){
            current = current.next;
         }

         // Once correct node has been located, new Node point to the next Node
         // (which happens to be at the desired index), then have the current node's
         // next field point to the new Node Client Program is trying to add
         newNode.next = current.next;
         current.next = newNode;
      }
      // After element is added, increment the size of the collection
      this.collectionSize++;
   }
   
   /* Return the element of the list at the given index. This operation
    * is only valid for 0 <= index < size(). This operation does not modify
    * the list. */
   public E get(int index) {

      // If index is greater than size or negative,
      // IllegalArguments Exception is thrown
      if (this.size() < index || index < 0) {
         throw new IllegalArgumentException();

         // Otherwise, the element at the desired index is found
         // and its stored value is returned
      } else {
         ListNode current = this.front;
         for (int i = 0; i < index; i++) {
            current = current.next;
         }
         return current.data;
      }
   }


   // Iterator method for iterating the object CS145LinkedList<E>
    @Override
    public Iterator<E> iterator(){
      Iterator<E> iterator = new Iterator<E>() {

         private ListNode current = front;
         private int index = 0;
         private int length = collectionSize;

         // Returns True if there are still non null elements in the list
         @Override
         public boolean hasNext(){
            return index < length;
         }

         // Returns the object stored in the ListNode that the Iterator
         // is pointing to, and updates the Iterator
         @Override
         public E next(){
            // Special case of the value of "front" ListNode being returned
            if (this.index == 0){
               this.index++;
               return current.data;
            }
            this.index++;
            this.current = this.current.next;
            return this.current.data;
         }

         // Do not have a remove method
         @Override
         public void remove() {
             throw new UnsupportedOperationException();
         }

      };
      return iterator;
   }

}