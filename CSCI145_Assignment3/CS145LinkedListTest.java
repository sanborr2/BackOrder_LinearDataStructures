/*
 * Student : Robert Sanborn
 * Date : March 2, 2017
 * File : CS145LinkedList.java
 *
 * WWU: CS145 Winter 2017, Assignment 3
 * CS145LinkedList.java -- A skeleton for a basic generic
 * linked list.
 *
 * Author: Chris Reedy (Chris.Reedy@wwu.edu).
 */

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.junit.*;
import org.junit.rules.Timeout;
import org.junit.runner.*;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;


public class CS145LinkedListTest {

   private static class TestPhase {
      String name;
      String description;
      int number;
      Class[] testClasses;
      
      TestPhase(String name, String desc, int number, Class... testClasses) {
         this.name = name;
         this.description = desc;
         this.number = number;
         this.testClasses = testClasses;
      }
   }

   private static final TestPhase[] testPhases =
         {new TestPhase("basic", "constructor, isEmpty, size", 2,
               BasicTests.class), // Test construction of lists
          new TestPhase("add to front", "add(value)", 6,
               AddTest.class),
          new TestPhase("get", "get", 3,
               GetTest.class),
          new TestPhase("add at index", "add(index, value)", 5,
               Add2Test.class)
         };
 
   // If true, there are no timeouts for tests, allowing tests to run
   // to completion
   private static boolean debugMode = false;
   
   // It true, failures in one test phase to not stop later phases from running
   private static boolean continueTests = false;
   
   private static final String COMMAND_LINE = "java CS145LinkedListTest [options]";
  
   public static void main(String[] args) {
      TestPhase[] phases = processArgs(args);
      if (phases != null) {
         for (TestPhase phase : phases) {
            String tests = (phase.number == 1) ? "test" : "tests";
            System.out.printf("Running phase %s: %s (%d %s)%n",
                  phase.name, phase.description, phase.number, tests);
            Boolean success = new TestRunner().run(phase.testClasses);
            System.out.println();
            if (!continueTests && !success) {
               System.out.println("Test failures: abandoning other phases.");
               System.exit(1);
            }
         }
         System.out.println("Congratulations! All tests passed.");
      }
   }
   
   public static TestPhase[] processArgs(String[] args) {
      Options options = new Options();
      options.addOption("d", "debug", false, "Debugging mode (no timeouts)");
      options.addOption("c", "continue", false, "Continue testing after failures");
      options.addOption("h", "help", false, "Print help message");
 
      CommandLine command;
      try {
         command = new GnuParser().parse(options, args);
      }
      catch (ParseException ex) {
         printUsage(ex.getMessage(), options);
         return null;
      }
      
      if (command.hasOption("help")) {
         printUsage(null, options);
         return null;
      }

      TestPhase[] cmdPhases;
      String[] commandArgs = command.getArgs();
      if (commandArgs.length > 0) {
         cmdPhases = new TestPhase[commandArgs.length];
         int phaseno = 0;
         for (String name : commandArgs) {
            TestPhase phase = findPhase(name);
            if (phase == null) {
               printUsage("Unrecognized phase name: " + name, options);
               return null;
            }
            cmdPhases[phaseno++] = phase;
         }
      } else {
         cmdPhases = testPhases;
      }
      
      if (command.hasOption("debug")) {
         debugMode = true;
         standardTimeout = null;
      }
      if (command.hasOption("continue")) {
         continueTests = true;
      }
      return cmdPhases;
   }
   
   static TestPhase findPhase(String name) {
      for (TestPhase phase : testPhases) {
         if (phase.name.equals(name)) {
            return phase;
         }
      }
      return null;
   }
   
   static void printUsage(String msg, Options options) {
      if (msg != null) {
         System.out.println(msg);
      }
      new HelpFormatter().printHelp(COMMAND_LINE, options);
   }
   
   static class TestRunner {
      public boolean run(Class<?>... classes) {
          JUnitCore core = new JUnitCore();
          core.addListener(new TestListener(System.out));
          Request req = Request.classes(classes);
          req = req.filterWith(Filter.ALL);
          Result result = core.run(req);
          printResult(System.out, result);
          return result.wasSuccessful();
      }
      
      public void printResult(PrintStream stream, Result result) {
         // Header
         stream.printf("Time: %.3f%n", result.getRunTime()/1000.0);

         // Print Failures
         List<Failure> failures = result.getFailures();
         if (failures.size() > 0) {
            stream.println();
            String format = (failures.size() == 1) ?
                  "There was %d failure:%n" :
                  "There were %d failures:%n";
            stream.printf(format, failures.size());
            int failNo = 0;
            for (Failure fail : failures) {
               stream.printf("%d) %s%n", ++failNo, fail.getTestHeader());
               Throwable ex = fail.getException();
               stream.println(ex);
               int ignored = 0;
               for (StackTraceElement elt : ex.getStackTrace()) {
                  String className = elt.getClassName();
                  if (className.startsWith("CS145LinkedList")
                        && !className.equals("CS145LinkedListTest$TestRunner")
                        && !(className.equals("CS145LinkedListTest")
                              && elt.getMethodName().equals("main"))) {
                     if (ignored != 0) {
                        stream.printf("        ... %d more%n", ignored);
                        ignored = 0;
                     }
                     stream.println("        at " + elt);
                  } else
                     ignored++;
               }
               // if (ignored != 0)
               //    stream.printf("        ... %d more%n", ignored);
            }
            stream.println();
         }
         
         // Footer
         int runCount = result.getRunCount();
         String tests = (runCount == 1) ? "test" : "tests";
         int ignoreCount = result.getIgnoreCount();
         String ignoreTests = (ignoreCount == 1) ? "test" : "tests";
         if (runCount == 0) {
            if (ignoreCount == 0)
               stream.printf("No tests were run.");
            else
               stream.printf("No tests were run (%d %s ignored.)",
                  ignoreCount, ignoreTests);
         } else {
            if (result.wasSuccessful())
               stream.printf("OK! (%d %s passed", runCount, tests);
            else
               stream.printf("Test Failed! (%d of %d %s failed",
                     result.getFailureCount(), runCount, tests);
            if (result.getIgnoreCount() != 0)
               stream.printf(", %d %s ignored", ignoreCount, ignoreTests);
            stream.println(".)");
         }
      }
   }
   
   static class TestListener extends RunListener {
      private final PrintStream stream;
      private boolean testStarted = false;
      
      public TestListener(PrintStream stream) {
         this.stream = stream;
      }
        
      @Override
      public void testRunStarted(Description description) {
         stream.append("Starting tests: ");
         testStarted = false;
      }
      
      @Override
      public void testRunFinished(Result result) {
         stream.println();
      }
      
      @Override
      public void testStarted(Description description) {
         testStarted = true;
      }
      
      @Override
      public void testFailure(Failure failure) {
         stream.append('E');
         testStarted = false;
      }
      
      @Override
      public void testFinished(Description description) {
         if (testStarted)
            stream.append('.');
         testStarted = false;
      }
      
      @Override
      public void testIgnored(Description description) {
         stream.append('I');
      }
   }
   
   public static final int DEFAULT_TIMEOUT = 20;
   
   public static Timeout standardTimeout = new Timeout(DEFAULT_TIMEOUT);
   
   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class BasicTests {   
   
      // Maximum 10 milliseconds for all tests
      @Rule public Timeout timeout = standardTimeout;

      @Test public void t01EmptyConstructor() {
         assertEquals("Empty constructor gives non-empty list.",
            true, new CS145LinkedList<String>().isEmpty());
      }
      
      @Test public void t02EmptyConstructorSize() {
         assertEquals("Empty constructor gives non-zero size.",
            0, new CS145LinkedList<String>().size());
      }
   }

   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class AddTest {   
   
      // Maximum 10 milliseconds for all tests
      @Rule public Timeout timeout = standardTimeout;
   
      @Test public void t11OneItemList() {
         CS145LinkedList<String> list = new CS145LinkedList<String>();
         list.add("value #1");
         assertEquals("get(0) on one element list is wrong.",
            false, list.isEmpty());
      }
   
      @Test public void t12OneItemListSize() {
         CS145LinkedList<String> list = new CS145LinkedList<String>();
         list.add("value #1");
         assertEquals("One element list has wrong size.",
            1, list.size());
      }
   
      @Test public void t13TwoItemList() {
         CS145LinkedList<String> list = new CS145LinkedList<String>();
         list.add("value #1");
         list.add("Value #2");
         assertEquals("Two element list is empty.",
            false, list.isEmpty());
      }
   
      @Test public void t14TwoItemListSize() {
         CS145LinkedList<String> list = new CS145LinkedList<String>();
         list.add("value #1");
         list.add("Value #2");
         assertEquals("Two element list has wrong size.",
            2, list.size());
      }
   
      @Test public void t15ThreeItemList() {
         CS145LinkedList<String> list = new CS145LinkedList<String>();
         list.add("value #1");
         list.add("Value #2");
         list.add("And a third value");
         assertEquals("Three element is empty.",
            false, list.isEmpty());
      }
   
      @Test public void t11ThreeItemListSize() {
         CS145LinkedList<String> list = new CS145LinkedList<String>();
         list.add("value #1");
         list.add("Value #2");
         list.add("And a third value");
         assertEquals("Three element list has wrong size.",
            3, list.size());
      }
   }

   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class GetTest {   
   
      // Maximum 10 milliseconds for all tests
      @Rule public Timeout timeout = standardTimeout;
   
      @Test public void t21OneItemList() {
         CS145LinkedList<String> list = new CS145LinkedList<String>();
         list.add("value #1");
         assertEquals("get(0) on one element list is wrong.",
            "value #1", list.get(0));
      }
   
   
      @Test public void t22TwoItemList() {
         CS145LinkedList<String> list = new CS145LinkedList<String>();
         list.add("value #1");
         list.add("Value #2");
         assertEquals("get(0) on two element list is wrong.",
            "value #1", list.get(0));
         assertEquals("get(1) on two element list is wrong.",
            "Value #2", list.get(1));
      }
   
      @Test public void t23ThreeItemList() {
         CS145LinkedList<String> list = new CS145LinkedList<String>();
         list.add("value #1");
         list.add("Value #2");
         list.add("And a third value");
         assertEquals("get(0) on three element list is wrong.",
            "value #1", list.get(0));
         assertEquals("get(1) on three element list is wrong.",
            "Value #2", list.get(1));
         assertEquals("get(2) on three element list is wrong.",
            "And a third value", list.get(2));
      }
   }

   @FixMethodOrder(org.junit.runners.MethodSorters.NAME_ASCENDING)
   public static class Add2Test {   
   
      // Maximum 10 milliseconds for all tests
      @Rule public Timeout timeout = standardTimeout;
   
      CS145LinkedList<Integer> nullList = null;
      CS145LinkedList<Integer> threeList = null;
   
      @Before public void setUp() {
         nullList = new CS145LinkedList<Integer>();
         threeList = new CS145LinkedList<Integer>();
         threeList.add(2);
         threeList.add(4);
         threeList.add(6);
      }

      @Test public void t31AddtoEmptyList() {
         nullList.add(0, 4);
         assertEquals("one element list is empty.",
            false, nullList.isEmpty());
         assertEquals("one element list has wrong size.",
            1, nullList.size());
         assertEquals("get(0) on one element list is wrong.",
            new Integer(4), nullList.get(0));
      }
         
      @Test public void t32AddtoThreeItemList0() {
         threeList.add(0, 1);
         assertEquals("four element list is empty.",
            false, threeList.isEmpty());
         assertEquals("four element list has wrong size.",
            4, threeList.size());
         assertEquals("get(0) on four element list is wrong.",
            new Integer(1), threeList.get(0));
         assertEquals("get(1) on four element list is wrong.",
            new Integer(2), threeList.get(1));
      }
   
      @Test public void t33AddtoThreeItemList1() {
         threeList.add(1, 3);
         assertEquals("four element list is empty.",
            false, threeList.isEmpty());
         assertEquals("four element list has wrong size.",
            4, threeList.size());
         assertEquals("get(0) on four element list is wrong.",
            new Integer(2), threeList.get(0));
         assertEquals("get(1) on four element list is wrong.",
            new Integer(3), threeList.get(1));
         assertEquals("get(2) on four element list is wrong.",
            new Integer(4), threeList.get(2));
      }
   
      @Test public void t34AddtoThreeItemList2() {
         threeList.add(2, 5);
         assertEquals("four element list is empty.",
            false, threeList.isEmpty());
         assertEquals("four element list has wrong size.",
            4, threeList.size());
         assertEquals("get(0) on four element list is wrong.",
            new Integer(4), threeList.get(1));
         assertEquals("get(1) on four element list is wrong.",
            new Integer(5), threeList.get(2));
         assertEquals("get(1) on four element list is wrong.",
            new Integer(6), threeList.get(3));
      }
   
      @Test public void t35AddtoThreeItemList3() {
         threeList.add(3, 7);
         assertEquals("four element list is empty.",
         false, threeList.isEmpty());
         assertEquals("four element list has wrong size.",
         4, threeList.size());
         assertEquals("get(1) on four element list is wrong.",
            new Integer(6), threeList.get(2));
         assertEquals("get(1) on four element list is wrong.",
            new Integer(7), threeList.get(3));
      }
   }   
}
