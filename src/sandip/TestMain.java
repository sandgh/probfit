/*
*
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%    ___  ___  ____  ___  ______________
%   / _ \/ _ \/ __ \/ _ )/ __/  _/_  __/
%  / ___/ , _/ /_/ / _  / _/_/ /  / /   
% /_/  /_/|_|\____/____/_/ /___/ /_/    
%                                      
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
*
*/

package sandip;

public class TestMain {

	public static void main(String[] args) {

		/**
		 * Tree i/p format - <node_index,node_val,parent_node_index>;<node_index,node_val,parent_node_index>; ...
		 **/
		//depth-2
		String ts1 = "0,-1; 1,0 ; 2,0; 3,1 ; 4,1 ; 5,2; 6,2; 7,2";
		
		//depth-1
//		String ts1 = " 0,-1; 1,0; 2,0; 3,0";
		
		//depth-2 (level-2 incomplete)
//		String ts1 = " 0,0,-1; 1,0,0; 2,0,0; 3,0,1; 4,0,2";
		
		Tree t1 = new Tree();
		TreeNode root1 = t1.loadTree(ts1);
//		System.out.println(t1);
		
		
		
//		String ts2 = "0,-1; 1,0 ; 2,0; 3,1 ; 4,1 ; 5,2; 6,2; 7,2";
		String ts2 = "0,-1; 1,0 ; 2,0; 13,1 ; 14,1 ; 15,2; 16,2; 17,2";
//		String ts2 = " 10,-1; 11,10 ; 12,10; 13,10";
//		String ts2 = " 0,10,-1; 1,11,0 ; 2,12,0; 3,13,1; 4,14,2";
		
		
		Tree t2 = new Tree();
		TreeNode root2 = t2.loadTree(ts2);
//		System.out.println(t2);
		
		//compare trees
		t1.compareTree(t2);
		
	}

}
