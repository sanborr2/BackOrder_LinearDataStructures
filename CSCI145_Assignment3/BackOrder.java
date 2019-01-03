/**
 * Student : Robert Sanborn
 * Date : March 2, 2017
 * File : CS145LinkedList.java
 * WWU: CS145 Winter 2017, Assignment 3
 */

public class BackOrder {

    // Customer with unfulfilled order
    private String customerNumber;

    // Number of Books still needed to be sent
    private int numOrders;

    // Constructor for BackOrder
    BackOrder(String customerNum, int numBooks){
        this.customerNumber = customerNum;
        this.numOrders = numBooks;
    }

    // Constructor where an other BackOrder object is passed
    private BackOrder(BackOrder other){
        this.customerNumber = other.customerNumber;
        this.numOrders = other.numOrders;
    }

    // Shows CustomerNumber BackOrder
    public String getCustomerNum() {
        return this.customerNumber;
    }

    // Changes the number of books that still need to be
    // sent to prospective customer
    public void changeNumOrders(int x){

        // If number of books being given to back order is greater
        // than needed number of books, exception is thrown
        if (x > this.numOrders) {
            throw new IllegalArgumentException();

        } else {
            this.numOrders -= x;
        }
    }

    // Return the number of books yet to be sent to
    // waiting customer
    public int getNumOrders() {
        return this.numOrders;
    }
}
