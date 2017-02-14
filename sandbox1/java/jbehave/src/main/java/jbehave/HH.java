package jbehave;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by atatarnikov on 07.06.16.
 */

//1.Какие типы коллекций вы знаете
//2.Расскажите вкратце о них (Set List Queue Map)
//3.Расскажите про Map какие у него типы
//4.Расскажите про Queue
//5.В каком случае применяется Queue
//6.дан список студентов найти уникального студента какой механизм вы тут будете применять (give a scratch not full realization)
//7.дан набор чисел найти 2 самых большых числа и поменять и поменять их местами (give a scratch and how you see the realization)
//8.Может ли принимать Queue коллекция значение нул

//ways to finish java program
//avrage arithmetics 200..400 - 50..100
//when object is collected  by garbage collector
//why in high load apps do not use catch for Exception
//why throwable catch only in exceptionable cases
//how to organize access of N threads to M resources with no Deadlock
//private static int x; psvm() {println("" + x++ + (x += 2))
//interface - long getID(), String getFieldName() - how to implement that interface in immutable, it it
//used as a key in MAP
//return sorted by string length arrays of strings

public class HH {
    public static void main(String[] args) throws Exception {
//        throw new Exception("sdfas");
//        Object h = new HH();
//        try {
//            System.out.println(h.hashCode());
//            h.wait();
//        } catch (InterruptedException e) {
//
//        } catch (IllegalMonitorStateException e) {
//
//        }
//        System.out.println("d");

        int[] list1 = {1, 3, 8, 1, 9, 2, 4, 6, 7};
        changeMax(list1);

    }

    public static void interSection1() {
        String[] g1, g2;

    }

    public static void changeMax(int[] numbers) {
        //sort collection
        //get 2 numbers

        List l = Arrays.asList(numbers);
        int i = 1;
    }

//    int secondLargest(int[] num) {
//        int max = num[0], prev_max = num[0];
//        //this loop to get max at the array
//        for (int i = 0; i < num.length; i++) {
//
//            if (num[i] > prev_max && num[i] < max) {
//                prev_max = max;
//                max = num[i];
//
//            }
//        }
//
//        return prev_max;
//    }
//
//
//
//    Node {
//        data
//        Node next;
//    }
//
//    a -> b -> c -> null
//            null <- a <- b <- c
//    Node reverse(Node head) {
//        Node current_node = head;
//
//        do {
//            Node next = current_node.next;
//            current_node = next;
//
//            //loop to get a 'c'
//        } while( next != null);
//
//    }

}
