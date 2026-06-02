package algorithm;

import model.Grievance;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * ============================================================
 *  GrievanceMinHeap — Array-backed Min-Heap from scratch
 *  NO java.util.PriorityQueue used!
 *
 *  Priority Score Formula:
 *    priority = (categoryWeight * 10) + ageBonus
 *    Higher score = MORE urgent (comes out first)
 *
 *    ageBonus = ageInDays  (every day overdue adds +1)
 *    If ageInDays > AGING_THRESHOLD: extra +1 per day beyond threshold
 *
 *  Min-heap on NEGATIVE priority so highest priority is extracted first.
 *  i.e., we store -priority so the "minimum" is the most urgent.
 * ============================================================
 */
public class GrievanceMinHeap {

    private static final int AGING_THRESHOLD = 7;   // days before aging kicks in
    private static final int MAX_CAPACITY    = 1000;

    private Grievance[] heap;
    private int size;

    public GrievanceMinHeap() {
        heap = new Grievance[MAX_CAPACITY];
        size = 0;
    }

    // ── Public API ───────────────────────────────────────────

    /**
     * Insert a grievance into the heap.
     * Calculates priority before inserting.
     */
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

        System.out.println("[Heap] Inserted: Grievance #" + g.getId()
                + " | Priority Score: " + score
                + " | Category: " + g.getCategoryName());
    }

    /**
     * Remove and return the highest priority grievance.
     */
    public Grievance extractMin() {
        if (size == 0) {
            System.out.println("[Heap] Heap is empty!");
            return null;
        }

        Grievance top = heap[0];          // highest priority
        heap[0] = heap[size - 1];         // move last to root
        heap[size - 1] = null;
        size--;
        percolateDown(0);                 // fix heap downward

        System.out.println("[Heap] Extracted: Grievance #" + top.getId()
                + " | Priority: " + top.getPriority());
        return top;
    }

    /**
     * Peek at top without removing.
     */
    public Grievance peek() {
        if (size == 0) return null;
        return heap[0];
    }

    public int getSize()    { return size; }
    public boolean isEmpty(){ return size == 0; }

    /**
     * Build heap from a list (used when loading all grievances from DB).
     */
    public void buildHeap(List<Grievance> grievances) {
        size = 0;
        for (Grievance g : grievances) {
            int score = calculatePriority(g);
            g.setPriority(score);
            heap[size++] = g;
        }
        // Heapify from last non-leaf to root
        for (int i = (size / 2) - 1; i >= 0; i--) {
            percolateDown(i);
        }
        System.out.println("[Heap] Built heap with " + size + " grievances.");
    }

    // ── Core Heap Operations ────────────────────────────────

    /**
     * Percolate UP: fix heap after insert.
     * Child with higher priority (higher score) bubbles up.
     */
    private void percolateUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            // We want HIGHER priority score at root → swap if child > parent
            if (heap[i].getPriority() > heap[parent].getPriority()) {
                swap(i, parent);
                i = parent;
            } else {
                break;
            }
        }
    }

    /**
     * Percolate DOWN: fix heap after extractMin.
     * Parent with lower priority sinks down.
     */
    private void percolateDown(int i) {
        while (true) {
            int left    = 2 * i + 1;
            int right   = 2 * i + 2;
            int largest = i;

            if (left < size && heap[left].getPriority() > heap[largest].getPriority()) {
                largest = left;
            }
            if (right < size && heap[right].getPriority() > heap[largest].getPriority()) {
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

    // ── Priority Formula ────────────────────────────────────

    /**
     * Priority Score = (categoryWeight × 10) + ageInDays + agingBonus
     *
     * Why this formula?
     * - Category weight gives base urgency (Water=3 → 30 base points)
     * - Age in days ensures old grievances naturally rise
     * - Aging bonus gives extra push after AGING_THRESHOLD days
     *   so even low-category grievances eventually reach the top
     */
    public static int calculatePriority(Grievance g) {
        long ageInDays = ChronoUnit.DAYS.between(g.getSubmittedDate(), LocalDate.now());
        if (ageInDays < 0) ageInDays = 0;

        int baseScore  = g.getCategoryWeight() * 10;
        int ageBonus   = (int) ageInDays;

        // Extra aging boost after threshold
        int agingBoost = 0;
        if (ageInDays > AGING_THRESHOLD) {
            agingBoost = (int)(ageInDays - AGING_THRESHOLD); // +1 per extra day
        }

        return baseScore + ageBonus + agingBoost;
    }

    // ── Debug / Display ─────────────────────────────────────

    /**
     * Print heap array state (useful for viva whiteboard trace).
     */
    public void printHeapArray() {
        System.out.println("\n[Heap Array State] size=" + size);
        System.out.println("─".repeat(60));
        for (int i = 0; i < size; i++) {
            System.out.printf("  [%d] GID=%d | %-10s | Priority=%d%n",
                    i,
                    heap[i].getId(),
                    heap[i].getCategoryName(),
                    heap[i].getPriority());
        }
        System.out.println("─".repeat(60));
    }

    /**
     * Get top N grievances without destroying heap.
     */
    public Grievance[] getTopN(int n) {
        // Temporarily extract n, then re-insert
        Grievance[] top = new Grievance[Math.min(n, size)];
        GrievanceMinHeap temp = new GrievanceMinHeap();

        // Copy heap
        for (int i = 0; i < size; i++) {
            Grievance copy = heap[i];
            temp.heap[i]   = copy;
        }
        temp.size = size;

        for (int i = 0; i < top.length; i++) {
            top[i] = temp.extractMin();
        }
        return top;
    }
}
