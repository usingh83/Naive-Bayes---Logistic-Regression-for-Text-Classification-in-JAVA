import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
public class NaiveBayes{
	public static Set<String> vocab=new HashSet<String>();
	public static TreeMap<String,Integer> spam_words=new TreeMap<String,Integer>();
	public static TreeMap<String,Integer> ham_words=new TreeMap<String,Integer>();
	public static Set<String> Stopwords=new HashSet<String>();
	public static void main(String[] args) throws Exception{
		String dir_location=args[0];
		String to_filter=args[1];
		
		File dir_spam_train=new File(dir_location+"train/spam");
		File dir_ham_train=new File(dir_location+"train/ham");
		File dir_spam_test=new File(dir_location+"test/spam");
		File dir_ham_test=new File(dir_location+"test/ham");
		File dir_stopwords=new File(dir_location+"stopwords.txt");
		
		
		String[] splsym={"!","#","%","^","&","*","(",")","!", ":",".","{","}", "[","]",">","<","?","/", "*","~", "@"};
		addDistinct(dir_spam_train);
		addDistinct(dir_ham_train);
		for(String s1:splsym){
			vocab.remove(s1);
		}		
		Scanner s=null;
		try{
			s=new Scanner(dir_stopwords);
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		while(s.hasNext()){
			String sw=s.next();
			Stopwords.add(sw);
		}
		s.close();
		if(to_filter.equals("yes")){
			System.out.println("Removing stop words....");
			for(String str:Stopwords){
				if(vocab.contains(str)){
					vocab.remove(str);
				}
			}
		}
		getHashmap_spam(dir_spam_train);
		getHashmap_ham(dir_ham_train);
		for(String s1:splsym){
			if(spam_words.containsKey(s1)){
				spam_words.remove(s1);
				}
			if(ham_words.containsKey(s1)){
				ham_words.remove(s1);
				}
			}
		if(to_filter.equals("yes")){
			for(String stopword:Stopwords){
				if(spam_words.containsKey(stopword)){
					spam_words.remove(stopword);
				}
				if(ham_words.containsKey(stopword)){
					ham_words.remove(stopword);
				}
			}
		}
		train(1);
		double priorSpam_probability=1.0*(dir_spam_train.listFiles().length)/(dir_spam_train.listFiles().length+dir_ham_train.listFiles().length);
		double priorHam_probability=1.0-priorSpam_probability;
		double log_priorSpam_probability=Math.log(priorSpam_probability);
		double log_priorHam_probability=Math.log(priorHam_probability);
		double num_correct_spam=0;
		int ns=0;
		for(File file:dir_spam_test.listFiles()){
			ns=ns+1;
			if(test_doc(file,log_priorHam_probability,log_priorSpam_probability,Stopwords,to_filter)==1){
				num_correct_spam=num_correct_spam+1.0;
			}
		}
		if(to_filter.equals("yes")){
			System.out.println("Accuracy of Naive Bayes after removal of Stop Words:");
		}
		else{
			System.out.println("Accuracy of Naive Bayes with out removing Stop Words: ");
		}
		System.out.println();
		double spam_accuracy=num_correct_spam/ns; 
		System.out.println("Spam % Accuracy "+spam_accuracy*100);
		double num_correct_ham=0;
		int nh=0;
		for(File file:dir_ham_test.listFiles()){
			nh=nh+1;
			if(test_doc(file,priorHam_probability,priorSpam_probability,Stopwords,to_filter)==0){
				num_correct_ham=num_correct_ham+1.0;
			}
		}
		System.out.println();
		double ham_accuracy=num_correct_ham/nh; 
		System.out.println("Ham % Accuracy : "+ ham_accuracy*100);
		System.out.println();
		}
	private static void getHashmap_spam(File dir_spam_train) throws Exception{
		for(File file:dir_spam_train.listFiles()){
			Scanner sc=new Scanner(file);
			while(sc.hasNext()){
				String line=sc.nextLine();
				for(String s:line.toLowerCase().trim().split(" ")){
					if(!s.isEmpty()){
						if(spam_words.containsKey(s)){
							spam_words.put(s,spam_words.get(s)+1);
						}else{
							spam_words.put(s,1);
						}
					}
				}
			}
			sc.close();
		}
		}
	private static void getHashmap_ham(File dir_ham_train) throws Exception{
		for(File file:dir_ham_train.listFiles()){
			Scanner sc=new Scanner(file);
			while(sc.hasNext()){
				String line=sc.nextLine();
				for(String s:line.toLowerCase().trim().split(" ")){
					if(!s.isEmpty()){
						if(ham_words.containsKey(s)){
							ham_words.put(s,ham_words.get(s)+1);
						}else{
							ham_words.put(s,1);
						}
					}	
				}
			}
			sc.close();
		}
	}
	private static void addDistinct(File dir_spam_train) throws Exception{
		System.out.println(dir_spam_train);
		for(File file:dir_spam_train.listFiles()){
			Scanner scanner=new Scanner(file);
			while(scanner.hasNext()){
				String line=scanner.nextLine();
				for(String s:line.toLowerCase().trim().split(" ")){
					if(!s.isEmpty()){
						vocab.add(s);
					}
				}
			}
			scanner.close();
			}
		}
	static HashMap<String,Double> spam_map_likelyhood=new HashMap<String,Double>();
	static HashMap<String,Double> ham_map_likelyhood=new HashMap<String,Double>();
	static int stotal=0;
	static int htotal=0;
	public static int train(int i){
		int spam_totalterms=0;
		for(Entry<String,Integer> entry:spam_words.entrySet()){
			spam_totalterms=spam_totalterms+entry.getValue();
		}
		int ham_totalterms=0;
		for(Entry<String,Integer> entry:ham_words.entrySet()){
			ham_totalterms=ham_totalterms+entry.getValue();
		}
		for(String s:vocab){
			if(spam_words.containsKey(s)){
				double spam_likely=(spam_words.get(s)+1.0)/(spam_totalterms+vocab.size()+1.0);
				double spam_loglikely=Math.log(spam_likely);
				spam_map_likelyhood.put(s,spam_loglikely);
			}			
		}
		for(String s:vocab){
			if(ham_words.containsKey(s)){
				double ham_likely=(ham_words.get(s)+1.0)/(ham_totalterms+vocab.size()+1.0);
				double ham_loglikely=Math.log(ham_likely);
				ham_map_likelyhood.put(s,ham_loglikely);
			}
			}
		stotal=spam_totalterms;
		htotal=ham_totalterms;
		return 1;
	}
	public int test_doc(File file,double priorHam_probability,double priorSpam_probability) throws Exception{
		double current_spamprob=0.0;
		double current_hamprob=0.0;
		Scanner scanner=new Scanner(file);
		while(scanner.hasNext()){
			String line=scanner.nextLine();
			for(String s:line.toLowerCase().split(" ")){
				if(spam_map_likelyhood.containsKey(s)){
						current_spamprob=current_spamprob+spam_map_likelyhood.get(s);
					}else{
						current_spamprob=current_spamprob+Math.log(1.0/(stotal+vocab.size()+1.0));
						}
					if(ham_map_likelyhood.containsKey(s)){
						current_hamprob=current_hamprob+ham_map_likelyhood.get(s);
					}else{
						current_hamprob=current_hamprob+Math.log(1.0/(htotal+vocab.size()+1.0));
					}
					}
		}
		scanner.close();
		current_spamprob=current_spamprob+priorSpam_probability;
		current_hamprob=current_hamprob+priorSpam_probability;
		if(current_spamprob>current_hamprob){
			return 1;
		}
		else{
			return 0;
		}
		}
	public static int test_doc(File file,double priorHam_probability,double priorSpam_probability,Set<String> stopword_list,String tofilter) throws Exception{
		double current_spamprob=0.0;
		double current_hamprob=0.0;
		Scanner scanner=new Scanner(file);
		while(scanner.hasNext()){
			String line=scanner.nextLine();
            if(tofilter.equals("yes")){
            	for(String s:line.toLowerCase().split(" ")){
            		if(!stopword_list.contains(s)){
    					if(spam_map_likelyhood.containsKey(s)){
    						current_spamprob=current_spamprob+spam_map_likelyhood.get(s);
    					}else{
    						current_spamprob=current_spamprob+Math.log(1.0/(stotal+vocab.size()+1.0));
    						}
    					if(ham_map_likelyhood.containsKey(s)){
    						current_hamprob=current_hamprob+ham_map_likelyhood.get(s);
    					}else{
    						current_hamprob=current_hamprob+Math.log(1.0/(htotal+vocab.size()+1.0));
    					}
            		}
    			}
            }
            else{
            	for(String s:line.toLowerCase().split(" ")){
            		if(spam_map_likelyhood.containsKey(s)){
    						current_spamprob=current_spamprob+spam_map_likelyhood.get(s);
    					}else{
    						current_spamprob=current_spamprob+Math.log(1.0/(stotal+vocab.size()+1.0));
    						}
    					if(ham_map_likelyhood.containsKey(s)){
    						current_hamprob=current_hamprob+ham_map_likelyhood.get(s);
    					}else{
    						current_hamprob=current_hamprob+Math.log(1.0/(htotal + vocab.size()+1.0));
    					}
    					}
            	}
            
		}
		scanner.close();
		current_spamprob=current_spamprob+priorSpam_probability;
		current_hamprob=current_hamprob+priorSpam_probability;
		if(current_spamprob>current_hamprob){
			return 1;
			}
		else{
			return 0;
		}
		}
	}