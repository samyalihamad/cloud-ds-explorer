package DataStructures;

public class NumArray {

    class SegmentNode {
        private SegmentNode leftNode;
        private SegmentNode rightNode;
        private int leftIndex;
        private int rightIndex;
        private int sum;

        public SegmentNode(int sum, int leftIndex, int rightIndex) {
            this.sum = sum;
            this.leftIndex = leftIndex;
            this.rightIndex = rightIndex;
        }

    }

    SegmentNode root;
    int[] range;
    public NumArray(int[] nums) {
        root = build(nums, 0, nums.length - 1);
        range = nums;
    }

    private SegmentNode build(int[] nums, int L, int R) {
        if(L == R)
            return new SegmentNode(nums[L], L, R);

        int Mid = (L + R) / 2;
        var currNode = new SegmentNode(0, L, R);
        currNode.leftNode = build(nums, L, Mid);
        currNode.rightNode = build(nums, Mid + 1, R);
        currNode.sum = currNode.leftNode.sum + currNode.rightNode.sum;
        return currNode;
    }

    public void update(int index, int val) {
        update(root, index, val);
    }

    private void update(SegmentNode node, int index, int val) {
        if(index > root.rightIndex)
            return;

        var curr = node;
        int currNum = range[index];
        int diff = val - currNum;

        range[index] = val;

        while(curr.leftIndex != index || curr.rightIndex != index) {
            curr.sum = curr.sum + diff;

            int Mid = (curr.leftIndex + curr.rightIndex) / 2;
            if(index <= Mid)
                curr = curr.leftNode;
            else
                curr = curr.rightNode;
        }

        curr.sum = curr.sum + diff;
    }

    public int sumRange(int left, int right) {
        return sumRange(root, left, right);
    }

    private int sumRange(SegmentNode node, int leftIndex, int rightIndex) {
        if(leftIndex == node.leftIndex && rightIndex == node.rightIndex)
            return node.sum;

        int Mid = (node.leftIndex + node.rightIndex) / 2;

        if(leftIndex > Mid)
            return sumRange(node.rightNode, leftIndex, rightIndex);
        else if(rightIndex <= Mid)
            return sumRange(node.leftNode, leftIndex, rightIndex);
        else
            return sumRange(node.leftNode, leftIndex, Mid) + sumRange(node.rightNode, Mid + 1, rightIndex);
    }
}