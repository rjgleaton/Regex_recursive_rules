//By RJ Gleaton for CSCE 355 Spring 2021
//Code takes in a test txt file of regular expressions
//and outputs a new string based on regex rules specified

import java.util.*;

class Node{

	private char data;
	private Node leftChild;
	private Node rightChild;

	public Node(char data){
		//super(data);
		this.data = data;
		this.rightChild = null;
		this.leftChild = null;
	}

	//Copy constructor
	public Node(Node copy){
		this.data = copy.getData();
		if(copy.getRightChild() != null)
			this.rightChild = new Node(copy.getRightChild());
		else
			this.rightChild = null;
		if(copy.getLeftChild() != null)
			this.leftChild = new Node(copy.getLeftChild());
		else
			this.leftChild = null;
	}

	public char getData(){
		return this.data;
	}
	public void setData(char data){
		this.data = data;
	}

	public Node getLeftChild(){
		return this.leftChild;
	}
	public void setLeftChild(Node childNode){
		this.leftChild = childNode;
	}

	public Node getRightChild(){
		return this.rightChild;
	}
	public void setRightChild(Node childNode){
		this.rightChild = childNode;
	}

}


public class regex {

	public static void main(String args[]){
		/** 
		System.out.println(args[0]);
		if(args[0].equals("--no-op")){
			System.out.println(no_op(args[1]));
		}**/
		Scanner scanner = new Scanner(System.in);
		while(scanner.hasNextLine()){
			String regExpression = scanner.nextLine();
			Stack<Node> expStack = getStack(regExpression);

			if(args[0].equals("--no-op"))
				System.out.println(no_op(expStack.pop()));
			else if(args[0].equals("--empty")){
				boolean truthVal = empty(expStack.pop());
				if(truthVal == true)
					System.out.println("yes");
				else
					System.out.println("no");
			}
			else if(args[0].equals("--has-epsilon")){
				boolean truthVal = has_epsilon(expStack.pop());
				if(truthVal == true)
					System.out.println("yes");
				else
					System.out.println("no");
			}
			else if(args[0].equals("--has-nonepsilon")){
				boolean truthVal = has_nonepsilon(expStack.pop());
				if(truthVal == true)
					System.out.println("yes");
				else
					System.out.println("no");
			}
			else if(args[0].equals("--infinite")){
				boolean truthVal = infinite(expStack.pop());
				if(truthVal == true)
					System.out.println("yes");
				else
					System.out.println("no");
			}
			else if(args[0].equals("--starts-with")){
				boolean truthVal = starts_with_a(expStack.pop(), args[1].charAt(0));
				if(truthVal == true)
					System.out.println("yes");
				else
					System.out.println("no");
			}
			else if(args[0].equals("--ends-with")){
				boolean truthVal = ends_with_a(expStack.pop(), args[1].charAt(0));
				if(truthVal == true)
					System.out.println("yes");
				else
					System.out.println("no");
			}
			else if(args[0].equals("--reverse")){
				System.out.println(reverse(expStack.pop()));
			}
			else if(args[0].equals("--prefixes")){
				System.out.println(prefix(expStack.pop()));
			}
			else if(args[0].equals("--suffixes")){
				System.out.println(suffix(expStack.pop()));
			}
			else if(args[0].equals("--b-before-a")){
				System.out.println(b_before_a(expStack.pop()));
			}
			else if(args[0].equals("--drop-one")){
				System.out.println(drop_one(expStack.pop()));
			}
			else if(args[0].equals("--strip")){
				System.out.println(strip(expStack.pop(), args[1].charAt(0)));
			}

		}
		scanner.close();
			
	}

	//Returns stack of tree nodes representing the abstract syntax tree of a regex
	public static Stack<Node> getStack(String regExp){
		Stack<Node> regStack = new Stack<Node>();
		char[] charRegex = regExp.toCharArray();

		for(int i = 0; i < charRegex.length; i++){

			if(charRegex[i] == '*'){
				Node kleene = new Node(charRegex[i]);
				kleene.setRightChild(regStack.pop());
				regStack.push(kleene);
			}
			else if(charRegex[i] == '+' || charRegex[i] == '.'){
				Node binary = new Node(charRegex[i]);
				binary.setRightChild(regStack.pop());
				binary.setLeftChild(regStack.pop());
				regStack.push(binary);
			}
			else{
				Node other = new Node(charRegex[i]);
				regStack.push(other);
			}	

		}

		return regStack;
	}

	//Prints out the prefix form of a regex given in postfix form
	public static String no_op(String regPost){

		Stack<Node> regStack = getStack(regPost);

		char prefix[] = new char[regPost.length()];
		for(int i = 0; i<prefix.length; i++){
			Node currNode = regStack.pop();
			prefix[i] = currNode.getData();

			if(currNode.getRightChild() != null)
				regStack.push(currNode.getRightChild());
			if(currNode.getLeftChild() != null)
				regStack.push(currNode.getLeftChild());
		}

		return String.valueOf(prefix);
	}

	//Overloaded no_op class that takes a root node instead of string
	public static String no_op(Node root){
		ArrayList<Character> charList = new ArrayList<Character>();
		Stack<Node> regExpStack = new Stack<Node>();
		regExpStack.push(root);
		
		while(!regExpStack.empty()){
			Node currNode = regExpStack.pop();
			charList.add(currNode.getData());

			if(currNode.getRightChild() != null)
			regExpStack.push(currNode.getRightChild());
			if(currNode.getLeftChild() != null)
			regExpStack.push(currNode.getLeftChild());
		}

		//Build String to return
		StringBuilder builder = new StringBuilder(charList.size());
		for(Character ch: charList)
			builder.append(ch);
		
		
		return builder.toString();
	} 


	public static boolean empty(Node currNode){
		
		boolean isEmpty = true;
		char currData = currNode.getData();

		if((currData != '/') && (currData != '+') && (currData != '.')){
			return false;
		}
		else{

			if(currData == '/')
				return true;
			if(currData == '+')
				isEmpty = (empty(currNode.getRightChild()) && empty(currNode.getLeftChild()));
			if(currData == '.')
				isEmpty = (empty(currNode.getRightChild()) || empty(currNode.getLeftChild()));

		}

		return isEmpty;
		
	}

	public static boolean has_epsilon(Node currNode){
		
		boolean hasEpsilon = true;
		char currData = currNode.getData();

		if(currData != '*' && currData != '+' && currData != '.'){
			return false;
		}
		else{

			if(currData == '*')
				return true;
			if(currData == '+')
				hasEpsilon = (has_epsilon(currNode.getRightChild()) || has_epsilon(currNode.getLeftChild()));
			if(currData == '.')
				hasEpsilon = (has_epsilon(currNode.getRightChild()) && has_epsilon(currNode.getLeftChild()));

		}

		return hasEpsilon;
		
	}

	public static boolean has_nonepsilon(Node currNode){

		boolean hasNonepsilon = false;
		char currData = currNode.getData();

		if(currData == '*')
			hasNonepsilon = has_nonepsilon(currNode.getRightChild());
		else if(currData == '/')
			return false;
		else if(currData == '+')
			hasNonepsilon = (has_nonepsilon(currNode.getLeftChild()) || has_nonepsilon(currNode.getRightChild()));			
		else if(currData == '.')
			hasNonepsilon = ((has_nonepsilon(currNode.getLeftChild()) || has_nonepsilon(currNode.getRightChild()))
							&& (!empty(currNode.getRightChild()) && (!empty(currNode.getLeftChild()))));
		else
			return true;

		return hasNonepsilon;

	}

	public static boolean infinite(Node currNode){

		boolean isInfinite = true;
		char currData = currNode.getData();

		if(currData != '+' && currData != '.' && currData != '*')
			return false;

		if(currData == '+')
			isInfinite = (infinite(currNode.getRightChild()) || infinite(currNode.getLeftChild()));
		if(currData == '.')
			isInfinite = ((infinite(currNode.getLeftChild()) && !empty(currNode.getRightChild()))
							|| (infinite(currNode.getRightChild()) && !empty(currNode.getLeftChild())));
		if(currData == '*')
			isInfinite = has_nonepsilon(currNode);

		return isInfinite;
	}

	public static boolean starts_with_a(Node currNode, char A){
		
		boolean starts = true;
		char currData = currNode.getData();
		//System.out.println(currNode.getRightChild().getData());

		if(currData == '/')
			return false;
		else if(currData == '+')
			starts = (starts_with_a(currNode.getLeftChild(), A) || starts_with_a(currNode.getRightChild(), A));
		else if(currData == '.')
			starts = ((starts_with_a(currNode.getLeftChild(), A) && !empty(currNode.getRightChild()))
						|| ((has_epsilon(currNode.getLeftChild()) && starts_with_a(currNode.getRightChild(), A))));
		else if(currData == '*')
			starts = starts_with_a(currNode.getRightChild(), A);
		else{
			if(currData == A)
				return true;
			return false;
		}
		return starts;
	}

	public static boolean ends_with_a(Node currNode, char A){

		boolean ends = false;
		char currData = currNode.getData();

		if(currData == '/')
			return false;
		else if(currData == '+')
			ends = (ends_with_a(currNode.getRightChild(), A) || ends_with_a(currNode.getLeftChild(), A));
		else if(currData == '.')
			ends = ((!empty(currNode.getLeftChild()) && ends_with_a(currNode.getRightChild(), A))
					|| ends_with_a(currNode.getLeftChild(), A) && has_epsilon(currNode.getRightChild()));
		else if (currData == '*')
			ends = ends_with_a(currNode.getRightChild(), A);
		else{
			if(currData == A)
				return true;
			return false;
		}
		return ends; 
	}

	public static String reverse(Node root){
		reverseHelper(root);
		return no_op(root);
	}
	public static void reverseHelper(Node currNode){
		char data = currNode.getData();

		if(data == '+'){
			reverseHelper(currNode.getLeftChild());
			reverseHelper(currNode.getRightChild());
		}
		else if(data == '.'){
			Node tempNode = currNode.getLeftChild();
			currNode.setLeftChild(currNode.getRightChild());
			currNode.setRightChild(tempNode);
			reverseHelper(currNode.getLeftChild());
			reverseHelper(currNode.getRightChild());
		}
		else if(data == '*')
			reverseHelper(currNode.getRightChild());
	}

	public static String prefix(Node root){
		prefixHelper(root);
		return no_op(root);
	}
	public static void prefixHelper(Node currNode){
		char currData = currNode.getData();

		// / -> /
		if(currData == '/'){
			return;
		}
		// s + t -> s' + t'
		else if(currData == '+'){ 
			prefixHelper(currNode.getLeftChild());
			prefixHelper(currNode.getRightChild());
		}
		// st -> / if Q0(t) or s' + st' else
		else if(currData =='.'){
			if(empty(currNode.getRightChild())){
				currNode.setData('/');
				currNode.setLeftChild(null);
				currNode.setRightChild(null);
			}
			else{
				Node leftCopy = new Node(currNode.getLeftChild());
				Node newRight = new Node('.');
				prefixHelper(currNode.getLeftChild());
				prefixHelper(currNode.getRightChild());
				currNode.setData('+');
				newRight.setLeftChild(leftCopy);
				newRight.setRightChild(currNode.getRightChild());
				currNode.setRightChild(newRight);
			}
		}
		// if Q0(s) s* -> /* or s* -> s*s' else
		else if(currData == '*'){
			if(empty(currNode.getRightChild()))
				currNode.getRightChild().setData('/');
			else{
				Node currCopy = new Node(currNode);
				//Node rightCopy = new Node(currNode.getRightChild());
				//currCopy.setRightChild(rightCopy);
				prefixHelper(currNode.getRightChild());
				currNode.setData('.');
				currNode.setLeftChild(currCopy);
				//currNode.setRightChild(childNode);
			}
		}
		// c -> c + /* 
		else{
			Node currCopy = new Node(currNode);
			currNode.setData('+');
			currNode.setLeftChild(currCopy);
			currNode.setRightChild(new Node('*'));
			currNode.getRightChild().setRightChild(new Node('/'));
		}
	}

	
	public static String suffix(Node root){
		suffixHelper(root);
		return no_op(root);
	}
	public static void suffixHelper(Node currNode){
		char currData = currNode.getData();

		// / -> /
		if(currData == '/'){}
		//s + t -> s' + t'
		else if(currData == '+'){
			suffixHelper(currNode.getLeftChild());
			suffixHelper(currNode.getRightChild());
		}
		//st -> / if Q0(s) or t'+s't
		else if(currData == '.'){
			if(empty(currNode.getLeftChild())){
				currNode.setData('/');
				currNode.setLeftChild(null);
				currNode.setRightChild(null);
			}
			else{
				Node rightCopy = new Node(currNode.getRightChild());
				Node newRight = new Node('.');

				suffixHelper(currNode.getLeftChild());
				suffixHelper(currNode.getRightChild());

				newRight.setLeftChild(currNode.getLeftChild());
				newRight.setRightChild(rightCopy);

				currNode.setData('+');
				currNode.setLeftChild(currNode.getRightChild());
				currNode.setRightChild(newRight);
			}
		}
		// s* -> /* if Q0(s) or s's* else
		else if(currData == '*'){
			if(empty(currNode.getRightChild()))
				currNode.getRightChild().setData('/');
			else{
				Node currCopy = new Node(currNode);
				suffixHelper(currNode.getRightChild());
				currNode.setData('.');
				currNode.setLeftChild(currNode.getRightChild());
				currNode.setRightChild(currCopy);
			}
		}
		// c -> c + /*
		else{
			Node currCopy = new Node(currNode);
			currNode.setData('+');
			currNode.setLeftChild(currCopy);
			currNode.setRightChild(new Node('*'));
			currNode.getRightChild().setRightChild(new Node('/'));
		}
	}


	public static String b_before_a(Node root){
		b_before_aHelper(root);
		return no_op(root);
	}
	public static void b_before_aHelper(Node currNode){
		char currData = currNode.getData();
		if(currData == 'a'){
			Node leftChild = new Node('b');
			Node rightChild = new Node('a');
			currNode.setData('.');
			currNode.setLeftChild(leftChild);
			currNode.setRightChild(rightChild);
		}
		else if(currData == '+' || currData == '.'){
			b_before_aHelper(currNode.getLeftChild());
			b_before_aHelper(currNode.getRightChild());
		}
		else if(currData == '*'){
			b_before_aHelper(currNode.getRightChild());
		}
	}

	public static String drop_one(Node root){
		drop_oneHelper(root);
		return no_op(root);
	}
	public static void drop_oneHelper(Node currNode){
		char currData = currNode.getData();

		if(currData == '+'){
			drop_oneHelper(currNode.getLeftChild());
			drop_oneHelper(currNode.getRightChild());
		}
		else if(currData == '.'){
			//Create .<-+->. structure
			currNode.setData('+');
			Node leftChild = new Node('.');
			Node rightChild = new Node('.');
			
			//Create copys of the children
			Node leftCopy = new Node(currNode.getLeftChild());
			Node rightCopy = new Node(currNode.getRightChild());

			drop_oneHelper(currNode.getLeftChild());
			drop_oneHelper(currNode.getRightChild());

			leftChild.setLeftChild(currNode.getLeftChild());
			leftChild.setRightChild(rightCopy);

			rightChild.setLeftChild(leftCopy);
			rightChild.setRightChild(currNode.getRightChild());

			currNode.setLeftChild(leftChild);
			currNode.setRightChild(rightChild);
		}
		else if(currData == '*'){
			Node rightCopy = new Node(currNode.getRightChild());
			Node newRight = new Node('.');
			Node currCopy = new Node('*');
			currCopy.setRightChild(rightCopy);

			currNode.setData('.');
			
			drop_oneHelper(currNode.getRightChild());
			newRight.setLeftChild(currNode.getRightChild());

			currNode.setLeftChild(currCopy);
			currNode.setRightChild(newRight);
			newRight.setRightChild(currCopy);

		}
		else if(currData == '/'){
			return;
		}
		else{
			currNode.setData('*');
			currNode.setRightChild(new Node('/'));
		}
	}

	public static String strip(Node root, char A){
		stripHelper(root, A);
		return no_op(root);
	}
	public static void stripHelper(Node currNode, char A){
		char currData = currNode.getData();

		if(currData == A){
			currNode.setData('*');
			currNode.setRightChild(new Node('/'));
		}
		else if(currData == '/'){}
		else if(currData == '+'){
			stripHelper(currNode.getLeftChild(), A);
			stripHelper(currNode.getRightChild(), A);
		}
		else if(currData == '.'){
			if(has_epsilon(currNode.getLeftChild())){
				//Node leftCopy = new Node(currNode.getLeftChild());
				Node rightCopy = new Node(currNode.getRightChild());

				stripHelper(currNode.getLeftChild(), A);
				stripHelper(currNode.getRightChild(), A);

				Node newLeft = new Node('.');
				newLeft.setLeftChild(currNode.getLeftChild());
				newLeft.setRightChild(rightCopy);

				currNode.setData('+');
				currNode.setLeftChild(newLeft);
			}
			else{
				stripHelper(currNode.getLeftChild(), A);
			}
		}
		else if(currData =='*'){
			Node currCopy = new Node(currNode);
			currNode.setData('.');
			stripHelper(currNode.getRightChild(), A);
			currNode.setLeftChild(currNode.getRightChild());
			currNode.setRightChild(currCopy);
		}
		else{
			currNode.setData('/');
		}
	}
}
