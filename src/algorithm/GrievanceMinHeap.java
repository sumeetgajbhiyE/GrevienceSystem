package algorithm;

import model.Grievance;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class GrievanceMinHeap {

    private static final int AGING_THRESHOLD = 7;
    private static final int MAX_CAPACITY = 1000;

    private Grievance[] heap;
    private int size;

    public GrievanceMinHeap() {
        heap = new Grievance[MAX_CAPACITY];
        size = 0;
    }

    // ==========================
    // Public API
    // ==========================
    public void insert(Grievance g) {

        if (size >= MAX_CAPACITY) {
            System.out.println("[Heap] Heap is full!");
            return;
        }

        int score = calculatePriority(g);
        g.setPriority(score);

        heap[size] = g;
        size++;

        percolateUp(size - 1);

        System.out.println(
                "[Heap] Inserted: Grievance #" + g.getId()
                + " | Priority Score: " + score
                + " | Category: " + g.getCategoryName()
        );
    }

    /**
     * Returns highest-priority grievance. (Implementation behaves as a Max
     * Heap)
     */
    public Grievance extractMin() {

        if (size == 0) {
            System.out.println("[Heap] Heap is empty!");
            return null;
        }

        Grievance top = heap[0];

        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;

        if (size > 0) {
            percolateDown(0);
        }

        System.out.println(
                "[Heap] Extracted: Grievance #" + top.getId()
                + " | Priority: " + top.getPriority()
        );

        return top;
    }

    public Grievance peek() {
        return (size == 0) ? null : heap[0];
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void buildHeap(List<Grievance> grievances) {

        size = 0;

        for (Grievance g : grievances) {

            if (size >= MAX_CAPACITY) {
                break;
            }

            int score = calculatePriority(g);
            g.setPriority(score);

            heap[size++] = g;
        }

        for (int i = (size / 2) - 1; i >= 0; i--) {
            percolateDown(i);
        }

        System.out.println(
                "[Heap] Built heap with " + size + " grievances."
        );
    }

    // ==========================
    // Heap Operations
    // ==========================
    private void percolateUp(int i) {

        while (i > 0) {

            int parent = (i - 1) / 2;

            if (heap[i].getPriority() > heap[parent].getPriority()) {

                swap(i, parent);
                i = parent;

            } else {
                break;
            }
        }
    }

    private void percolateDown(int i) {

        while (true) {

            int left = 2 * i + 1;
            int right = 2 * i + 2;

            int largest = i;

            if (left < size
                    && heap[left].getPriority()
                    > heap[largest].getPriority()) {

                largest = left;
            }

            if (right < size
                    && heap[right].getPriority()
                    > heap[largest].getPriority()) {

                largest = right;
            }

            if (largest != i) {

                swap(i, largest);
                i = largest;

            } else {
                break;
            }
        }
    }

    private void swap(int a, int b) {

        Grievance temp = heap[a];
        heap[a] = heap[b];
        heap[b] = temp;
    }

    // ==========================
    // Remove by ID
    // ==========================
    public boolean remove(int grievanceId) {

        int index = -1;

        for (int i = 0; i < size; i++) {

            if (heap[i].getId() == grievanceId) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false;
        }

        heap[index] = heap[size - 1];
        heap[size - 1] = null;

        size--;

        if (index < size) {
            percolateDown(index);
            percolateUp(index);
        }

        System.out.println(
                "[Heap] Removed Grievance #" + grievanceId
        );

        return true;
    }

    // ==========================
    // Top N Grievances
    // ==========================
    public Grievance[] getTopN(int n) {

        int count = Math.min(n, size);

        Grievance[] top = new Grievance[count];

        GrievanceMinHeap temp = new GrievanceMinHeap();

        for (int i = 0; i < size; i++) {
            temp.heap[i] = heap[i];
        }

        temp.size = size;

        for (int i = 0; i < count; i++) {
            top[i] = temp.extractMin();
        }

        return top;
    }

    // ==========================
    // Priority Formula
    // ==========================
    public static int calculatePriority(Grievance g) {

        long ageInDays = ChronoUnit.DAYS.between(
                g.getSubmittedDate(),
                LocalDate.now()
        );

        if (ageInDays < 0) {
            ageInDays = 0;
        }

        int baseScore = g.getCategoryWeight() * 10;
        int ageBonus = (int) ageInDays;

        int agingBoost = 0;

        if (ageInDays > AGING_THRESHOLD) {
            agingBoost = (int) (ageInDays - AGING_THRESHOLD);
        }

        return baseScore + ageBonus + agingBoost;
    }

    // ==========================
    // Debug Display
    // ==========================
    public void printHeapArray() {

        System.out.println("\n[Heap Array State] size=" + size);
        System.out.println("--------------------------------------------------");

        for (int i = 0; i < size; i++) {

            System.out.printf(
                    "[%d] GID=%d | %-10s | Priority=%d%n",
                    i,
                    heap[i].getId(),
                    heap[i].getCategoryName(),
                    heap[i].getPriority()
            );
        }

        System.out.println("--------------------------------------------------");
    }
}
