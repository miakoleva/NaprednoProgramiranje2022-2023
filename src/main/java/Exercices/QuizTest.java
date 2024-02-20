package Exercices;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class QuizTest {
    public static void main(String[] args){

        Scanner sc = new Scanner(System.in);

        Quiz quiz = new Quiz();

        int questions = Integer.parseInt(sc.nextLine());

        for (int i=0;i<questions;i++) {
            try {
                quiz.addQuestion(sc.nextLine());
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }

        }

        List<String> answers = new ArrayList<>();

        int answersCount =  Integer.parseInt(sc.nextLine());

        for (int i=0;i<answersCount;i++) {
            answers.add(sc.nextLine());
        }

        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase==1) {
            quiz.printQuiz(System.out);
        } else if (testCase==2) {
            try {
                quiz.answerQuiz(answers, System.out);
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }

        } else {
            System.out.println("Invalid test case");
        }
    }
}


abstract class Question{

    String type;
    String text;
    int points;
    String answer;

    public Question(String type, String text, int points, String answer) {
        this.type = type;
        this.text = text;
        this.points = points;
        this.answer = answer;
    }

    public int getPoints() {
        return points;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public String toString() {
        //Multiple Choice Question: Question2 Points 4 Answer: E
        if(type.equals("TF")){
            type = "True/False Question";
            return String.format("%s: %s Points: %d Answer: %s", type, text, points, answer);
        }
        else{
            type = "Multiple Choice Question";
            return String.format("%s: %s Points %d Answer: %s", type, text, points, answer);
        }

    }

    abstract double calculatePoints(String correct, Question question);


}

class TFQuestion extends Question{

    public TFQuestion(String type, String text, int points, String answer) {
        super(type, text, points, answer);
    }

    @Override
    double calculatePoints(String correct, Question question) {
        if(correct.equals(question.getAnswer()))
            return question.getPoints();
        else
            return 0;
    }


}

class MCQuestion extends Question{

    public MCQuestion(String type, String text, int points, String answer) {
        super(type, text, points, answer);
    }

    @Override
    double calculatePoints(String correct, Question question) {
        if(correct.equals(question.getAnswer()))
            return question.getPoints();
        else
            return (-question.getPoints()*0.2);
    }


}

class Quiz{
    List<Question> questions;
    List<Question> checkAnswers;

    public Quiz() {
        this.questions = new ArrayList<>();
        this.checkAnswers = new ArrayList<>();
    }


    void addQuestion(String questionData) throws InvalidOperationException {

        String []parts = questionData.split(";");
        String type = parts[0];
        String text = parts[1];
        int points = Integer.parseInt(parts[2]);
        String answer = parts[3];

        if(type.equals("TF")){
            Question question = new TFQuestion(type, text, points, answer);
            questions.add(question);
            checkAnswers.add(question);
        }else if(type.equals("MC")){

            if(!(answer.equals("A") || answer.equals("B") || answer.equals("C") || answer.equals("D") || answer.equals("E")))
                throw new InvalidOperationException(String.format("%s is not allowed option for this question", answer));
            Question question = new MCQuestion(type, text, points, answer);
            questions.add(question);
            checkAnswers.add(question);
        }
    }

    void printQuiz(OutputStream os){
        PrintWriter pw = new PrintWriter(os);
        Comparator<Question> comparator =
                Comparator.comparing(Question::getPoints).reversed();

        questions.stream().sorted(comparator)
                .forEach(q -> pw.println(q.toString()));

        pw.flush();
    }

    void answerQuiz (List<String> answers, OutputStream os) throws InvalidOperationException {
        PrintWriter pw = new PrintWriter(os);
        double total = 0;

        if(answers.size()!=checkAnswers.size())
            throw new InvalidOperationException("Answers and questions must be of same length!");

        for(int i = 0; i<answers.size(); i++){
            pw.printf("%d. %.2f\n", i+1, checkAnswers.get(i).calculatePoints(answers.get(i), checkAnswers.get(i)));
            total+=checkAnswers.get(i).calculatePoints(answers.get(i), checkAnswers.get(i));
        }

        pw.printf("Total points: %.2f", total);



        pw.flush();
    }
}


class InvalidOperationException extends Exception{
    public InvalidOperationException(String message) {
        super(message);
    }

}