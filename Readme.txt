Classification of e-mail as spam or ham based on Naive Bayes and Logistic Regression

-------------------*Naive Bayes*---------------------------

Steps to Compile and run the program:

Naive Bayes: Download and unzip the folder containg the files of Naive Bayes program : NaiveBayes.java

Argument 0 - folder containing test + train folders + SpamWords.txt 
Argument 1 - yes or no to indicate whether to consider ( Yes - Remove Stop word ; No - Do not remove Stop words)

Step 1: javac NaiveBayes.java

Step 2: java NaiveBayes path_to_folder_conataining_trainingfolder_and_testfolder_and_stopword.txt yes/no

In the above case it would be;

Example: java NaiveBayes F:\temp no

-------------------*Logistic Regression*---------------------------

Similarly with Logistic Regression for the same folder structure above:

Argument 0 : path to folder containing above folders- test and train + stopword.txt file 
Argument 1: to_filter_stopwords: yes or no 
Argument 2: learning_rate_eta 
Argument 3: lambda 
Argument 4: num_iterations

Step1 : javac LogisticsRegression.java 
Step 2 : 
java LogisticsRegression "path_of_data_files" yes_or_no 0.01 .01 10 1000
 
Ex: java LogisticsRegression "C:/Users/kanwa/Desktop/Assignment2" no 0.01 .01 10 1000
