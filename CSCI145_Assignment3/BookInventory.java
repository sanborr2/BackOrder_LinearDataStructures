/**
 * * Student : Robert Sanborn
 * Date : March 2, 2017
 * File : CS145LinkedList.java
 * WWU: CS145 Winter 2017, Assignment 3
 */

import java.io.*;
import java.util.*;

public class BookInventory {

    public static class ImproperFilePassed extends IllegalArgumentException{
        public ImproperFilePassed(String msg){
            super(msg);
        }
    }


    // Main
    // Expectations:
    // 1st commandline argument is filename Books.txt, Books1.txt,
    // or a comparable file
    // 2nd commandline argument is filename Transactions.txt, Transactions1.txt,
    // or a comparable file
    public static void main(String[] args) throws FileNotFoundException {
        // check for the number of command line arguments
        if(!processArgs(args)){
            System.out.println("Usage: BookInventory input_file_names (2)");

            // If files are readable begin operations
        } else {
            CS145LinkedList<BookData> inventory = new CS145LinkedList<>();
            double totalSales;

            // Build up inventory using data from initial file,
            // then, Show the data of all BookData objects currently
            // in the inventory
            File initialInventory = new File(args[0]);
            initializeInventory(initialInventory, inventory);

            // Begin order and stock calls on inventory and
            // add necessary changes to inventory
            File OrderCalls = new File(args[1]);
            totalSales = beginInventoryCalls(inventory, OrderCalls);

            System.out.println();
            System.out.printf("Total value of orders filled is $%.2f\n", totalSales);
        }
    }

    // Check to see if filenames (2) were passed and if the files can be read
    private static boolean processArgs(String[] args){

        // Check to see if there are two files names that can be read
        if(args.length != 2){

            System.out.println("Please enter two file names:" +
                    " Books, Transactions or Books1, Transaction1");
            System.out.println("Or compatible files");

            return false;

            // Check to see if the two files can be opened
        } else {
            for (int x = 0; x < 2; x++){
                // Make a file object to represent txt file to be read
                File inputFile = new File(args[x]);
                if (!inputFile.canRead()) {
                    System.out.println("The file " + args[x]
                            + " cannot be opened for input.");
                    return false;
                }
            }
            // At this point we know the files are readable
            return true;
        }
    }

    // Initializes inventory of books for store
    // Expectations:
    // File object passed is initial listing of inventory
    // such as Books.txt, Books1.txt, or at least a comparable file
    public static void initializeInventory(File initialInventory,
                                           CS145LinkedList<BookData> inventory) {
        Scanner input = null;

        // Try opening file object for initial listing of inventory
        try {
            input = new Scanner(initialInventory);

            // If failure occurs throw exception
        } catch (FileNotFoundException ex) {
            System.out.println("Error: File " + initialInventory.getName()
                    + "not found. Terminating Program.");
        }
        // Otherwise begin reading file
        while(input.hasNextLine()) {

            String line = input.nextLine();
            Scanner lineScanner = new Scanner(line);

            // Read lines of txt file as tokes delimited by commas
            lineScanner.useDelimiter(",");

            // Try reading tokens of lines and save data to BookData Objects
            // of inventory
            try {
                String title = lineScanner.next();
                String ISBN = lineScanner.next();
                double price = lineScanner.nextDouble();
                String format = lineScanner.next();
                int stock = lineScanner.nextInt();

                // Create new book object using collected data
                Book aNewBook = new Book(title,ISBN,price,format);
                aNewBook.changeStock(stock);

                // Pass created Book object to new BookData object
                inventory.add(new BookData(aNewBook));

                // If failure occurs trying to save tokens in given way occurs,
                // throw InputMismatchException
            } catch (InputMismatchException ex) {
                System.out.println("Line : " + line);
                System.out.println("Mismatched Token" + lineScanner.next());
            }
        }
    }


    // Prints to Screen list of all books, and relevant information, in inventory
    public static void SHOW(CS145LinkedList<BookData> inventory) {

        System.out.println();

        // Iterate through all the BookData Objects in the CSLinkedList<BookData>
        Iterator<BookData> itr = inventory.iterator();
        while( itr.hasNext() ) {
            BookData currentBKD = itr.next();

            // Print to Screen a statement containing all current
            // data of the book object inside the BookData Object
            currentBKD.BookInventoryStatement();

            // Iterate through all BackOrder objects saved inside
            // the ArrayDeque of the BookData Object if any
            for (BackOrder order : currentBKD.getBackOrders()) {
                System.out.println("Backorders:");
                System.out.print("  customer: " + order.getCustomerNum());
                System.out.print(", amount: " + order.getNumOrders() + "\n");
            }
        }
        System.out.println();
    }


    // Fills out order for specified book if possible, otherwise generates back order
    public static double ORDER(int numOrderedBooks, BookData currentBKD,
                               String customerNum){
        boolean isASale = true;
        double newSale;

        // Satisfy customer order if possible
        if(numOrderedBooks <= currentBKD.getBook().getStock()){
            currentBKD.getBook().changeStock(-1*numOrderedBooks);

            System.out.print("Order filled ");
            newSale = orderDescription(numOrderedBooks, currentBKD, customerNum,
                    isASale);

            // At this point back order occurs, save to memory any revenue of sales
            // from partially resolving back order
        } else {
            newSale = createBackOrder(currentBKD, customerNum, numOrderedBooks);
        }
        // Return revenue from sales if any
        return newSale;
    }

    // Generates Back Order and returns value of any revenue
    // from partially satisfying order
    public static double createBackOrder(BookData bookInfo, String customerNum,
                                         int numOrderedBooks) {
        boolean isASale = true;
        double newSale = 0.0;

        // Send all books in inventory to customer then create back order
        int initialBooksSent = bookInfo.getBook().getStock();
        bookInfo.getBook().changeStock(-1 * initialBooksSent);

        // If any books can be sent, print notification to screen and store
        // revenue from sale of partially resolving BackOrder to memory
        if (initialBooksSent != 0) {
            System.out.print("Order filled ");
            newSale = orderDescription(initialBooksSent, bookInfo,
                    customerNum, isASale);
        }
        // Create New back order for the specified book
        int booksNeeded = (numOrderedBooks - initialBooksSent);
        bookInfo.addBackOrder(customerNum, booksNeeded);

        // Print to screen notification about back order
        isASale = false;
        System.out.print("Back order ");
        orderDescription(booksNeeded, bookInfo, customerNum, isASale);

        return newSale;
    }


    // Method that prints to Screen part of an Order or Back order notification
    // while returning revenue from new sale, if any
    public static double orderDescription(int numBooks, BookData bookInfo,
                                          String customerNum, boolean isASale){

        // If there is only one book copy being sent out or
        // listed as a back order, print following statement
        if (numBooks == 1){
            System.out.println("for customer " + customerNum +
                    " for 1 copy of book " + bookInfo.BookDescription());

            // Otherwise, if more than one book copy is being sent out or
            // listed as a back order, print following statement
        } else {
            System.out.println("for customer "+ customerNum + " for " +
                    numBooks + " copies of book " + bookInfo.BookDescription());
        }
        // If a sale occurred return revenue from sale, otherwise return 0
        if(isASale){
            return numBooks * bookInfo.getBook().getPrice();
        } else {
            return 0;
        }
    }

    // Replenishes stock of specified book, then resolves as many back orders
    // on book as possible in order of least to most recent
    public static double STOCK(int numMoreBooks, BookData currentBKD) {

        // Increase stock of book specified and print notification to screen
        int newStock = numMoreBooks + currentBKD.getBook().getStock();
        System.out.println("Stock for book " + currentBKD.BookDescription() +
                " increased from " + currentBKD.getBook().getStock() +
                " to " + newStock);
        currentBKD.getBook().changeStock(numMoreBooks);

        // Return revenue from sales if any
        return resolveBackOrders(currentBKD);
    }

    // Satisfies Back Orders for book as much as possible and
    // returns value of any revenue from partially satisfying order
    public static double resolveBackOrders(BookData bookInfo){

        int currentStock = bookInfo.getBook().getStock();
        double newSales = 0;

        // If there are back orders resolve them in order
        // of oldest to most recent

        while (bookInfo.areThereBackOrders() ) {
            BackOrder oldestBKOD = bookInfo.getOldestBackOrder();

            boolean backOrderFilled = true;

            // If new stock is not enough to resolve back order,
            // send all new stock to customer
            if (currentStock - oldestBKOD.getNumOrders() < 0){
                oldestBKOD.changeNumOrders(currentStock);

                // Print notification to Screen about
                // oldest back order being partially satisfied
                System.out.print("Back order filled ");
                newSales += orderDescription(currentStock, bookInfo,
                        oldestBKOD.getCustomerNum(), backOrderFilled);

                bookInfo.getBook().changeStock(-1*currentStock);
                break;

                // Otherwise resolve back order
            } else {
                // Update Stock of specified book
                int booksSent = oldestBKOD.getNumOrders();
                currentStock -= booksSent;
                bookInfo.getBook().changeStock(-1*booksSent);

                // Print notification to Screen
                // about oldest back order being satisfied
                System.out.print("Back order filled ");
                newSales += orderDescription(booksSent, bookInfo,
                        oldestBKOD.getCustomerNum(), backOrderFilled);

                // Delete oldest back order
                bookInfo.terminateOldestBackOrder();
            }
        }
        // Return revenue from sales if any
        return newSales;
    }


    // Begins transactions on book inventory for store
    // Expectations:
    // File object passed is a listing of transactions on books in inventory
    // such as Transactions.txt, Transactions1.txt, or at least a comparable file
    // Also inventory at this point has non null objects
    public static double beginInventoryCalls(CS145LinkedList<BookData> inventory,
                                             File OrderCalls) {

        // Only begin transactions if inventory has BookData Objects
        if (!inventory.isEmpty()) {
            Scanner input = null;

            // Try opening file object for initial listing of inventory
            try {
                input = new Scanner(OrderCalls);

                // If failure occurs throw exception
            } catch (FileNotFoundException ex) {
                System.out.println("Error: File " + OrderCalls.getName()
                        + "not found. Terminating Program.");
            }

            double salesToDate = 0.0;
            // Otherwise begin reading file
            while (input.hasNextLine()) {
                String line = input.nextLine();
                Scanner lineScanner = new Scanner(line);

                // Expectation:
                // first token is one of three Strings, either SHOW, STOCK, or ORDER
                String command = lineScanner.next();

                // If command is SHOW, print to screen a statement displaying data
                // of a BookData object for every BookData object in the list
                if (command.equals("SHOW")) {
                    SHOW(inventory);

                    // Else if STOCK or ORDER is called, read the following tokens
                } else if (command.equals("STOCK") || command.equals("ORDER")) {

                    // Try reading ISBN, additional books, and in the case of an order,
                    // customer ID number, a certain BookData Object

                    // If tokens are successfully stored either SHOW, ORDER, or STOCK
                    // should be called
                    try {
                        String ISBNofBook = lineScanner.next();
                        int numBooks = lineScanner.nextInt();

                        BookData currentBKD;
                        boolean bookFound = false;

                        // Iterate through all the BookData Objects in inventory until
                        // BookData object with matching ISBN is found
                        Iterator<BookData> itr = inventory.iterator();
                        while (itr.hasNext()) {
                            currentBKD = itr.next();

                            // If ISBN of Book Object inside BookData object currentBKD
                            // has the same ISBN, update stock of book
                            if (currentBKD.getISBNofBook().equals(ISBNofBook)) {
                                bookFound = true;
                                // Else, if command is STOCK, resolve as many backorders
                                // as possible if there are any, then add any remaining
                                // stock to BookData object
                                if (command.equals("STOCK")) {
                                    salesToDate += STOCK(numBooks, currentBKD);

                                    // Else, if command is ORDER, take books out of
                                    // inventory and complete orders till order is
                                    // satisfied or back order is incurred
                                } else {
                                    // Read the ID number of prospective customer
                                    String customerNum = lineScanner.next();

                                    // If tokens are successfully stored, call ORDER
                                    salesToDate += ORDER(numBooks, currentBKD,
                                            customerNum);
                                }
                            }
                        }

                        // If there is no book in inventory with the same ISBN
                        // Output message to screen
                        if(!bookFound){
                            System.out.println("Book with ISBN " + ISBNofBook +
                                    " not found in inventory");
                        }

                        // If failure occurs trying to save tokens in given way occurs,
                        // throw InputMismatchException
                    } catch (InputMismatchException ex) {
                        System.out.println("Line : " + line);
                        System.out.println("Mismatched Token" + lineScanner.next());
                    }

                    // At this point we know the file being read must be an improper file
                } else {
                    throw new ImproperFilePassed("second commandline argument" +
                            " must be Transactions.txt, Transactions1.txt," +
                            " or comparable file");
                }
            }
            // Return all revenue accrued from sales during transactions iteration
            return salesToDate;

            // At this point we know inventory was an empty inventory
        } else {
            throw new IllegalArgumentException();
        }
    }
}
