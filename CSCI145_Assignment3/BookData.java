/**
 * Student : Robert Sanborn
 * Date : March 2, 2017
 * File : CS145LinkedList.java
 * WWU: CS145 Winter 2017, Assignment 3
 */

import java.util.*;

public class BookData {

    private ArrayDeque<BackOrder> UnfulfilledOrders;
    private Book aBook;
    private String ISBN;

    // Constructor with only Book field passed
    public BookData(Book aBook){
        this.aBook = aBook;
        this.UnfulfilledOrders = new ArrayDeque<>();
        this.ISBN = aBook.getIsbn();
    }

    // Return true if there are back orders, otherwise return false
    public boolean areThereBackOrders(){
        return !UnfulfilledOrders.isEmpty();
    }

    // When back order happens, add back order to the back of
    // the ArrayDeque containing the BackOrders
    public void addBackOrder(String customerNum, int numBooks){
        BackOrder aBackOrder = new BackOrder(customerNum, numBooks);
        this.UnfulfilledOrders.addLast(aBackOrder);
    }

    // When the oldest back order, and thus the most prioritized one,
    // is fulfilled, remove it from the ArrayDeque
    public void terminateOldestBackOrder() {
        this.UnfulfilledOrders.removeFirst();
    }

    // Obtain back order that needs to be fulfilled first, the oldest
    public BackOrder getOldestBackOrder() {
        return this.UnfulfilledOrders.getFirst();
    }

    // Obtain the Book Object in the BookData object
    public Book getBook(){
        return this.aBook;
    }

    // Return ISBN of book
    public String getISBNofBook(){
        return this.ISBN;
    }

    // Obtain the ArrayDeque Object of the back orders
    public ArrayDeque<BackOrder> getBackOrders(){
        return this.UnfulfilledOrders;
    }


    // Print statement about book description, price, and stock
    public void BookInventoryStatement() {
        String returnStatement = "Book: " + this.BookDescription();
        System.out.printf(returnStatement + ", Price: %.2f, Stock: %d", aBook.getPrice(), aBook.getStock());
        System.out.println();
    }

    // Returns String containing book's title, ISBN, and format
    public String BookDescription(){
        return this.getBook().getTitle() + " " + this.getBook().getIsbn() +
                " " + this.getBook().getFormat();
    }
}
