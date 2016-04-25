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
//		String ts1 = "0,-1; 1,0 ; 2,0; 3,1 ; 4,1 ; 5,2; 6,2; 7,2";
		
		//depth-1
		String ts1 = "0,-1; 1,0; 2,0; 3,1; 4,1; 5,2; 6,2;";
//		String ts1 = " 0,-1; 1,0; 2,0; 3,0;";
		
		
		//depth-2 (level-2 incomplete)
//		String ts1 = " 0,0,-1; 1,0,0; 2,0,0; 3,0,1; 4,0,2";
		
//		StringBuffer sb = new StringBuffer();
//		sb.append("0,-1");
//		for(int i=1;i<10;i++)	sb.append("; "+i+",0");
//		System.out.println(sb.toString());
		
//		Tree t1 = new Tree();
		Tree_v2 t1 = new Tree_v2();
		TreeNode root1 = t1.loadTree(ts1);
//		TreeNode root1 = t1.loadTree(sb.toString());
//		System.out.println(t1);
		
		
		
//		String ts2 = "0,-1; 1,0 ; 2,0; 3,1 ; 4,1 ; 5,2; 6,2; 7,2";
//		String ts2 = "0,-1; 1,0 ; 2,0; 3,1 ; 4,1 ; 15,2; 16,2; 17,2";
		String ts2 = "0,-1; 12,0; 11,0; 14,1; 13,1; 15,2; 16,2;";
//		String ts2 = " 0,-1; 11,0 ; 12,0; 13,0;";
//		String ts2 = " 0,10,-1; 1,11,0 ; 2,12,0; 3,13,1; 4,14,2";
		
//		sb.delete(4, sb.length());
//		for(int i=1;i<10;i++)	sb.append("; "+i+",0");
//		System.out.println(sb.toString());
		
//		Tree t2 = new Tree();
		Tree_v2 t2 = new Tree_v2();
		TreeNode root2 = t2.loadTree(ts2);
//		TreeNode root2 = t2.loadTree(sb.toString());
//		System.out.println(t2);
		
		//compare trees
		t1.compareTree(t2);
		
	}

}
