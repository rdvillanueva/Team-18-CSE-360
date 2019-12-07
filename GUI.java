import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import java.util.ArrayList;
import java.io.File;
import java.io.*;
import javafx.event.ActionEvent;
import java.util.Scanner;
/*
 *
 */

class Gui extends VBox{

    //analytic area labels
    private Label meanLabel ;
    private Label medianLabel;
    private Label modeLabel;

    //distribution area labels
    private Label grp09;
    private Label grp1019;
    private Label grp2029;
    private Label grp3039;
    private Label grp4049;
    private Label grp5059;
    private Label grp6069;
    private Label grp7079;
    private Label grp8089;
    private Label grp9099;

    //file selection menu
    private FileChooser fileChooser;

    //core data list
    private ArrayList<Double> data;

    //track label pointers, for delete and redraw
    private ArrayList<Label> labels;

    //various labels
    private Label display;
    private Label analyze;
    private Label distributions;

    //sub panels -
    private VBox displayBox;		//lower left child - display area
    private VBox analysisBox;		//lower middle child - analysis area
    private VBox distributionBox;	//lower right child - distrivution area
    private HBox calcPanel;		//lower area parent, parent of the 3 vboxes above
    private HBox keyBoardBox;	//top area, above buttons, add-delete-keyboard inputs
    private HBox chartBox;			//putting display graph button in an hbox, so we can append graph to it's right

    //keyboard input field
    private TextField keyBoardInput;

    //buttons
    private Button addData;
    private Button deleteData;
    private Button loadFile;
    private Button appendFile;
    private Button displayGraph;
    private Button setBoundaries;
    private Button createReport;
    private double high;
    private double low;
    private double mean;
    private double median;
    private double mode;
    private double localLow;
    private double localHigh;

    public Gui(){
        //analytic labels
        Label meanLabel = new Label("mean: 0");
        Label medianLabel = new Label("median:0");
        Label modeLabel = new Label("mode:0");

        //distribution labels
        Label grp09 = new Label("0%-9%");
        Label grp1019 = new Label("10%-19%");
        Label grp2029 = new Label("20%-29%");
        Label grp3039 = new Label("30%-39%");
        Label grp4049 = new Label("40%-49%");
        Label grp5059 = new Label("50%-59%");
        Label grp6069 = new Label("60%-69%");
        Label grp7079 = new Label("70%-79%");
        Label grp8089 = new Label("80%-89%");
        Label grp9099 = new Label("90%-100%");

        //initialize default range
        low = 0;
        high = 100;

        //file menu search
        fileChooser = new FileChooser();

        //core data list
        data = new ArrayList<Double>();

        //label storage/tracking
        labels = new ArrayList<Label>();

        //various labels
        Label display = new Label("----Display Area----");
        Label analyze = new Label("----Analysis Area----");
        Label distributions = new Label("----Distribution Area----");

        //sub panel initialize
        displayBox = new VBox(display);	//lower left
        chartBox = new HBox();			//graph button box, nests inside this.vbox with the other buttons
        analysisBox = new VBox(analyze);	//lower middle
        analysisBox.getChildren().addAll(meanLabel,medianLabel,modeLabel);	//lower middle
        distributionBox = new VBox(distributions);//lower right
        distributionBox.getChildren().addAll(grp09,grp1019,grp2029,grp3039,grp4049,grp5059,grp6069,grp7079,grp8089,grp9099);//lower right
        calcPanel = new HBox(); //parent of lower panels (display area,analysis area,distribution area)
        keyBoardBox = new HBox();//sub panel keyboard input - inserted at top of program

        //key input field
        keyBoardInput = new TextField("");

        //buttons
        addData = new Button("Add data");
        deleteData = new Button("Delete data");
        loadFile = new Button("Load File");
        appendFile = new Button("Append File");
        displayGraph = new Button("Display Graph");
        setBoundaries = new Button("Set Boundaries");
        createReport = new Button("Create Report");

        //assign event handlers
        addData.setOnAction(new AddDataHandler());
        deleteData.setOnAction(new DeleteDataHandler());
        loadFile.setOnAction(new LoadFileHandler());
        appendFile.setOnAction(new AppendFileHandler());
        displayGraph.setOnAction(new DisplayGraphHandler());
        setBoundaries.setOnAction(new SetBoundariesHandler());
        createReport.setOnAction(new CreateReportHandler());

        //add children to sub panels
        keyBoardBox.getChildren().addAll(keyBoardInput,addData,deleteData);
        calcPanel.getChildren().addAll(displayBox,analysisBox,distributionBox);
        chartBox.getChildren().add(displayGraph);

        //initialize main panel with all sub panels and buttons
        getChildren().addAll(keyBoardBox,loadFile,appendFile,chartBox,setBoundaries,createReport,calcPanel);
    }

    //class functions, none needed?
    public void checkIntegrity(){
        //does nothing...for now...
    }


    /*
     *
     *All trigger events handled in the nested classes below
     *
     */


    /*
     *	take single input from keyboard and adds to list
     */
    private class AddDataHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            try{
                //get input
                String in = keyBoardInput.getText();
                System.out.println(in);

                //check if it's in bounds
                if(isInRange(in)){
                    /*
                     * Redrawing of display area and update of data list
                     */
                    data.add(Double.parseDouble(in));
                    redraw(data);
                }

            }
            catch(Exception e){
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("Input Error");
                a.setContentText("Input must be a number");
                a.show();
            }

        }

        /*
         *@param String, takes a String and parses it as a Double
         *@return boolean, telling us if number is in boundary range
         */
        public boolean isInRange(String in){
            if(Double.parseDouble(in) >= low && Double.parseDouble(in) <= high){
                System.out.println("Number added to list");
                for(int i = 0; i < labels.size();i++){	//orm
                    displayBox.getChildren().add(labels.get(i));
                }
                return true;
            }
            else
                System.out.println("Number Is not in range, not added to list");
            return false;
        }

        /*
         *@param Double, takes in our core data set and redraws the panels
         *
         */
        public void redraw(ArrayList<Double> temp){
            displayBox.getChildren().clear();
            Label title = new Label("----Display Area----");
            displayBox.getChildren().add(title);
            labels.clear();
            int columns = (data.size()/4)+1;
            int col1 = 0+columns;
            int col2 = col1+columns;
            int col3 = col2+columns;
            VBox column1 = new VBox();
            VBox column2 = new VBox();
            VBox column3 = new VBox();
            VBox column4 = new VBox();
            for(int i = 0; i < data.size();i++){
                Label ormsby = new Label(" "+data.get(i)+" ");
                labels.add(ormsby);
                if(i < col1){
                    column1.getChildren().add(labels.get(i));
                }
                else if(i < col2){
                    column2.getChildren().add(labels.get(i));
                }
                else if(i < col3){
                    column3.getChildren().add(labels.get(i));
                }
                else{
                    column4.getChildren().add(labels.get(i));
                }
            }
            //add columns to display box
            HBox colContainer = new HBox(column1,column2,column3,column4);
            displayBox.getChildren().add(colContainer);

            //calculate mean
            double sum = 0;
            for(int i = 0; i < data.size(); i++){
                sum += data.get(i);
            }
            mean = sum/data.size();
            meanLabel = new Label("mean: "+mean);
            //calculate median
            median = getMedian(data);
            medianLabel = new Label("median: "+median);
            //calculate mode

            if(data.size() >2)
                mode = getMode(data);
            if(data.size() ==1)
                mode = data.get(0);
            if(data.size() ==0)
                mode = 0;
            modeLabel = new Label("mode: "+mode);

            //DO ANALYSIS
            analysisBox.getChildren().clear();
            int zero = 0;
            int one = 0;
            int two = 0;
            int three = 0;
            int four = 0;
            int five = 0;
            int six = 0;
            int seven = 0;
            int eight = 0;
            int nine = 0;
            int ten = 0;


            for(int i = 0; i< data.size(); i++){
                Double num = data.get(i)/high;
                if( num < .10d)
                    zero++;
                else if(num < .20d)
                    two++;
                else if(num< .30d)
                    three++;
                else if(num < .40d)
                    four++;
                else if(num< .50d)
                    five++;
                else if(num< .60d)
                    six++;
                else if(num < .70d)
                    seven++;
                else if(num< .80d)
                    eight++;
                else if(num< .90d)
                    nine++;
                else
                    ten++;

            }

            grp09 = new Label("0%-9%:"+zero);
            grp1019 = new Label("10%-19%:"+two);
            grp2029 = new Label("20%-29%:"+three);
            grp3039 = new Label("30%-39%:"+four);
            grp4049 = new Label("40%-49%:"+five);
            grp5059 = new Label("50%-59%:"+six);
            grp6069 = new Label("60%-69%:"+seven);
            grp7079 = new Label("70%-79%:"+eight);
            grp8089 = new Label("80%-89%:"+nine);
            grp9099 = new Label("90%-100%:"+ten);
            distributionBox.getChildren().clear();
            Label dist = new Label("----Distribution Area----");
            distributionBox.getChildren().addAll(dist,grp09,grp1019,grp2029,grp3039,grp4049,grp5059,grp6069,grp7079,grp8089,grp9099);//lower right
            //FINISH ANALYSIS

            Label analyze = new Label("----Analysis Area----");
            analysisBox.getChildren().add(analyze);
            analysisBox.getChildren().addAll(meanLabel,medianLabel,modeLabel);
        }















        /*
         * @param data set,
         * @return median by insertion sort and selection of middle element
         */
        public Double getMedian(ArrayList<Double> temp){
            //insertion sort
            for(int i = 1; i < temp.size(); i++){
                Double key = temp.get(i);
                int j = i -1;
                while( j >= 0  && temp.get(j) > key){
                    temp.set((j+1),temp.get(j));
                    j--;
                }
                temp.set((j+1),key);
            }
            //print
            for(int i = 0; i < temp.size(); i++){
                System.out.println(temp.get(i));
            }
            return temp.get((temp.size()-1)/2);
        }

        public Double getMode(ArrayList<Double> temp){
            int cout = 0;
            int newCout = 0;
            Double max = temp.get(0);
            Double newMax = temp.get(1);
            //get count of first element
            for(int i = 1; i < temp.size(); i++){
                if(max.equals( temp.get(i))){
                    cout++;
                }
            }
            System.out.println("curent max "+max+ " with cout of "+ cout);
            //count element 2
            for(int i = 1; i < temp.size(); i++){
                max = temp.get(i);
                for(int j = 1; j < temp.size(); j++){
                    if(max.equals( temp.get(j))){
                        newCout++;
                        System.out.println("new max "+ temp.get(j)+ " with cout of "+ newCout);
                    }
                }
                if(newCout > cout){
                    cout = newCout;
                    newMax = temp.get(i);
                }
                newCout = 0;
            }

            System.out.println("new max "+newMax+ " with cout of "+ newCout);

            return newMax;
        }

    }


    /*
     *	take single input from keyboard and deletes from list
     */
    private class DeleteDataHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            try{
                String in = keyBoardInput.getText();
                System.out.println(in);
                boolean isDeleted = data.remove(Double.parseDouble(in));
                if(isDeleted){
                    System.out.println("Number removed");
                    //this is where we update the labels
                    for(int i = 0; i < labels.size();i++){
                        if( Double.parseDouble(labels.get(i).getText())  == Double.parseDouble(in) )      {
                            labels.remove(i);
                            break;
                        }
                    }
                    //redraw the set
                    redraw(data);
                }
                else
                    System.out.println("Number not removed, it's not in list");
            }
            catch(NumberFormatException e){
                System.out.println("Input must be a number");
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("Delete Error");
                a.setContentText("Input must be a number");
                a.show();
            }
            catch(Exception ignored){

            }
        }
        public Double getMedian(ArrayList<Double> temp){
            //insertion sort
            for(int i = 1; i < temp.size(); i++){
                Double key = temp.get(i);
                int j = i -1;
                while( j >= 0  && temp.get(j) > key){
                    temp.set((j+1),temp.get(j));
                    j--;
                }
                temp.set((j+1),key);
            }
            //print
            for(int i = 0; i < temp.size(); i++){
                System.out.println(temp.get(i));
            }
            return temp.get((temp.size()-1)/2);
        }

        //
        public Double getMode(ArrayList<Double> temp){
            int cout = 0;
            int newCout = 0;
            Double max = temp.get(0);
            Double newMax = temp.get(1);
            //get count of first element
            for(int i = 1; i < temp.size(); i++){
                if(max.equals( temp.get(i))){
                    cout++;
                }
            }
            System.out.println("curent max "+max+ " with cout of "+ cout);
            //count element 2
            for(int i = 1; i < temp.size(); i++){
                max = temp.get(i);
                for(int j = 1; j < temp.size(); j++){
                    if(max.equals( temp.get(j))){
                        newCout++;
                        System.out.println("new max "+ temp.get(j)+ " with cout of "+ newCout);
                    }
                }
                if(newCout > cout){
                    cout = newCout;
                    newMax = temp.get(i);
                }
                newCout = 0;
            }

            System.out.println("new max "+newMax+ " with cout of "+ newCout);

            return newMax;
        }
        /*
         *@param Double, takes in our core data set and redraws the panels
         *
         */
        public void redraw(ArrayList<Double> temp){
            displayBox.getChildren().clear();
            Label title = new Label("----Display Area----");
            displayBox.getChildren().add(title);
            labels.clear();
            int columns = (data.size()/4)+1;
            int col1 = 0+columns;
            int col2 = col1+columns;
            int col3 = col2+columns;
            VBox column1 = new VBox();
            VBox column2 = new VBox();
            VBox column3 = new VBox();
            VBox column4 = new VBox();
            for(int i = 0; i < data.size();i++){
                Label ormsby = new Label(" "+data.get(i)+" ");
                labels.add(ormsby);
                if(i < col1){
                    column1.getChildren().add(labels.get(i));
                }
                else if(i < col2){
                    column2.getChildren().add(labels.get(i));
                }
                else if(i < col3){
                    column3.getChildren().add(labels.get(i));
                }
                else{
                    column4.getChildren().add(labels.get(i));
                }
            }
            //add columns to display box
            HBox colContainer = new HBox(column1,column2,column3,column4);
            displayBox.getChildren().add(colContainer);

            //calculate mean
            double sum = 0;
            for(int i = 0; i < data.size(); i++){
                sum += data.get(i);
            }
            mean = sum/data.size();
            meanLabel = new Label("mean: "+mean);
            //calculate median
            median = getMedian(data);
            medianLabel = new Label("median: "+median);
            //calculate mode
            if(data.size() >2)
                mode = getMode(data);
            if(data.size() ==1)
                mode = data.get(0);
            if(data.size() ==0)
                mode = 0;
            modeLabel = new Label("mode: "+mode);

            //DO ANALYSIS
            analysisBox.getChildren().clear();
            int zero = 0;
            int one = 0;
            int two = 0;
            int three = 0;
            int four = 0;
            int five = 0;
            int six = 0;
            int seven = 0;
            int eight = 0;
            int nine = 0;
            int ten = 0;


            for(int i = 0; i< data.size(); i++){
                Double num = data.get(i)/high;
                if( num < .10d)
                    zero++;
                else if(num < .20d)
                    two++;
                else if(num< .30d)
                    three++;
                else if(num < .40d)
                    four++;
                else if(num< .50d)
                    five++;
                else if(num< .60d)
                    six++;
                else if(num < .70d)
                    seven++;
                else if(num< .80d)
                    eight++;
                else if(num< .90d)
                    nine++;
                else
                    ten++;

            }
            grp09 = new Label("0%-9%"+zero);
            grp1019 = new Label("10%-19%:"+two);
            grp2029 = new Label("20%-29%:"+three);
            grp3039 = new Label("30%-39%:"+four);
            grp4049 = new Label("40%-49%:"+five);
            grp5059 = new Label("50%-59%:"+six);
            grp6069 = new Label("60%-69%:"+seven);
            grp7079 = new Label("70%-79%:"+eight);
            grp8089 = new Label("80%-89%:"+nine);
            grp9099 = new Label("90%-100%:"+ten);
            distributionBox.getChildren().clear();
            Label dist = new Label("----Distribution Area----");
            distributionBox.getChildren().addAll(dist,grp09,grp1019,grp2029,grp3039,grp4049,grp5059,grp6069,grp7079,grp8089,grp9099);//lower right
            //FINISH ANALYSIS

            Label analyze = new Label("----Analysis Area----");
            analysisBox.getChildren().add(analyze);
            analysisBox.getChildren().addAll(meanLabel,medianLabel,modeLabel);
        }

    }

    //loads multiple inputs to data from a .txt or.csv file
    private class LoadFileHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            File file = fileChooser.showOpenDialog(new Stage());
            try{
                System.out.println(file.getName());
            } catch (NullPointerException ignored){
                return;
            }
            data.clear();
            try{
                fileHandling handler = new fileHandling(file);
                data = handler.getNumberList();
            }
            catch(IllegalFileName | IOException e){
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Imported file has an illegal file extension.");
                a.show();

            } catch (Exception err){
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(err.getMessage());
                a.show();
            }
            finally{
                if (data.size() == 0){
                    Alert empty = new Alert(Alert.AlertType.WARNING);
                    empty.setContentText("No data in the file.");
                    empty.show();
                } else{
                    redraw(data);
                }

            }
        }
        public void redraw(ArrayList<Double> temp){
            displayBox.getChildren().clear();
            Label title = new Label("----Display Area----");
            displayBox.getChildren().add(title);
            labels.clear();
            int columns = (data.size()/4)+1;
            int col1 = 0+columns;
            int col2 = col1+columns;
            int col3 = col2+columns;
            VBox column1 = new VBox();
            VBox column2 = new VBox();
            VBox column3 = new VBox();
            VBox column4 = new VBox();
            for(int i = 0; i < data.size();i++){//jorms
                Label ormsby = new Label(" "+data.get(i)+" ");
                labels.add(ormsby);
                if(i < col1){
                    column1.getChildren().add(labels.get(i));
                }
                else if(i < col2){
                    column2.getChildren().add(labels.get(i));
                }
                else if(i < col3){
                    column3.getChildren().add(labels.get(i));
                }
                else{
                    column4.getChildren().add(labels.get(i));
                }
            }

            HBox colContainer = new HBox(column1,column2,column3,column4);
            displayBox.getChildren().add(colContainer);

            //calculate mean
            double sum = 0;
            for(int i = 0; i < data.size(); i++){
                sum += data.get(i);
            }
            mean = sum/data.size();
            meanLabel = new Label("mean: "+mean);
            median = getMedian(data);
            medianLabel = new Label("median: "+median);
            //calculate mode
            if(data.size() >2)
                mode = getMode(data);
            if(data.size() ==1)
                mode = data.get(0);
            if(data.size() ==0)
                mode = 0;
            modeLabel = new Label("mode: "+mode);

            //DO ANALYSIS
            analysisBox.getChildren().clear();
            int zero = 0;
            int one = 0;
            int two = 0;
            int three = 0;
            int four = 0;
            int five = 0;
            int six = 0;
            int seven = 0;
            int eight = 0;
            int nine = 0;
            int ten = 0;

            for(int i = 0; i< data.size(); i++){
                Double num = data.get(i)/high;
                if( num < .10d)
                    zero++;
                else if(num < .20d)
                    two++;
                else if(num< .30d)
                    three++;
                else if(num < .40d)
                    four++;
                else if(num< .50d)
                    five++;
                else if(num< .60d)
                    six++;
                else if(num < .70d)
                    seven++;
                else if(num< .80d)
                    eight++;
                else if(num< .90d)
                    nine++;
                else
                    ten++;

            }
            grp09 = new Label("0%-9%:"+zero);
            grp1019 = new Label("10%-19%:"+two);
            grp2029 = new Label("20%-29%:"+three);
            grp3039 = new Label("30%-39%:"+four);
            grp4049 = new Label("40%-49%:"+five);
            grp5059 = new Label("50%-59%:"+six);
            grp6069 = new Label("60%-69%:"+seven);
            grp7079 = new Label("70%-79%:"+eight);
            grp8089 = new Label("80%-89%:"+nine);
            grp9099 = new Label("90%-100%:"+ten);
            distributionBox.getChildren().clear();
            Label dist = new Label("----Distribution Area----");
            distributionBox.getChildren().addAll(dist,grp09,grp1019,grp2029,grp3039,grp4049,grp5059,grp6069,grp7079,grp8089,grp9099);//lower right
            //FINISH ANALYSIS

            Label analyze = new Label("----Analysis Area----");
            analysisBox.getChildren().add(analyze);
            analysisBox.getChildren().addAll(meanLabel,medianLabel,modeLabel);
        }
        public Double getMode(ArrayList<Double> temp){
            int cout = 0;
            int newCout = 0;
            Double max = temp.get(0);
            Double newMax = temp.get(1);
            //get count of first element
            for(int i = 1; i < temp.size(); i++){
                if(max.equals( temp.get(i))){
                    cout++;
                }
            }
            System.out.println("curent max "+max+ " with cout of "+ cout);
            //count element 2
            for(int i = 1; i < temp.size(); i++){
                max = temp.get(i);
                for(int j = 1; j < temp.size(); j++){
                    if(max.equals( temp.get(j))){
                        newCout++;
                        System.out.println("new max "+ temp.get(j)+ " with cout of "+ newCout);
                    }
                }
                if(newCout > cout){
                    cout = newCout;
                    newMax = temp.get(i);
                }
                newCout = 0;
            }

            System.out.println("new max "+newMax+ " with cout of "+ newCout);

            return newMax;
        }
        public Double getMedian(ArrayList<Double> temp){
            //insertion sort
            for(int i = 1; i < temp.size(); i++){
                Double key = temp.get(i);
                int j = i -1;
                while( j >= 0  && temp.get(j) > key){
                    temp.set((j+1),temp.get(j));
                    j--;
                }
                temp.set((j+1),key);
            }
            //print
            for(int i = 0; i < temp.size(); i++){
                System.out.println(temp.get(i));
            }
            return temp.get((temp.size()-1)/2);
        }
    }
    //appends multiple inputs, from a .txt or .csv file, to a data list
    private class AppendFileHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            File file = fileChooser.showOpenDialog(new Stage());
            try {
                System.out.println(file.getName());
            } catch (NullPointerException ignored){
                return;
            }
            fileHandling File = new fileHandling(file);
            try {
                ArrayList<Double> dataTemp = File.getNumberList();
                data.addAll(dataTemp);
            } catch (IllegalFileName err) {
                Alert illegal = new Alert(Alert.AlertType.ERROR);
                illegal.setContentText(err.getMessage());
                illegal.show();
                return;
            } catch (IOException e) {
                return;
            }

            if (data.size() != 0) {
                redraw(data);
            }
        }
        public void redraw(ArrayList<Double> temp){
            displayBox.getChildren().clear();
            Label title = new Label("----Display Area----");
            displayBox.getChildren().add(title);
            labels.clear();
            int columns = (data.size()/4)+1;
            int col2 = columns +columns;
            int col3 = col2+columns;
            VBox column1 = new VBox();
            VBox column2 = new VBox();
            VBox column3 = new VBox();
            VBox column4 = new VBox();
            for(int i = 0; i < data.size();i++){//jorms
                Label ormsby = new Label(" "+data.get(i)+" ");
                labels.add(ormsby);
                if(i < columns){
                    column1.getChildren().add(labels.get(i));
                }
                else if(i < col2){
                    column2.getChildren().add(labels.get(i));
                }
                else if(i < col3){
                    column3.getChildren().add(labels.get(i));
                }
                else{
                    column4.getChildren().add(labels.get(i));
                }
            }

            HBox colContainer = new HBox(column1,column2,column3,column4);
            displayBox.getChildren().add(colContainer);

            //calculate mean
            double sum = 0;
            for (Double aDouble : data) {
                sum += aDouble;
            }
            mean = sum/data.size();
            meanLabel = new Label("mean: "+mean);
            median = getMedian(data);
            medianLabel = new Label("median: "+median);
            //calculate mode
            if(data.size() >2)
                mode = getMode(data);
            if(data.size() ==1)
                mode = data.get(0);
            if(data.size() ==0)
                mode = 0;
            modeLabel = new Label("mode: "+mode);

            //DO ANALYSIS
            analysisBox.getChildren().clear();
            int zero = 0;
            int one = 0;
            int two = 0;
            int three = 0;
            int four = 0;
            int five = 0;
            int six = 0;
            int seven = 0;
            int eight = 0;
            int nine = 0;
            int ten = 0;


            for (Double datum : data) {
                double num = datum / high;
                if (num < .10d)
                    zero++;
                else if (num < .20d)
                    two++;
                else if (num < .30d)
                    three++;
                else if (num < .40d)
                    four++;
                else if (num < .50d)
                    five++;
                else if (num < .60d)
                    six++;
                else if (num < .70d)
                    seven++;
                else if (num < .80d)
                    eight++;
                else if (num < .90d)
                    nine++;
                else
                    ten++;

            }
            grp09 = new Label("0%-9%"+zero);
            grp1019 = new Label("10%-19%:"+two);
            grp2029 = new Label("20%-29%:"+three);
            grp3039 = new Label("30%-39%:"+four);
            grp4049 = new Label("40%-49%:"+five);
            grp5059 = new Label("50%-59%:"+six);
            grp6069 = new Label("60%-69%:"+seven);
            grp7079 = new Label("70%-79%:"+eight);
            grp8089 = new Label("80%-89%:"+nine);
            grp9099 = new Label("90%-100%:"+ten);
            distributionBox.getChildren().clear();
            Label dist = new Label("----Distribution Area----");
            distributionBox.getChildren().addAll(dist,grp09,grp1019,grp2029,grp3039,grp4049,grp5059,grp6069,grp7079,grp8089,grp9099);//lower right
            //FINISH ANALYSIS

            Label analyze = new Label("----Analysis Area----");
            analysisBox.getChildren().add(analyze);
            analysisBox.getChildren().addAll(meanLabel,medianLabel,modeLabel);
        }
        public Double getMedian(ArrayList<Double> temp){
            //insertion sort
            for(int i = 1; i < temp.size(); i++){
                Double key = temp.get(i);
                int j = i -1;
                while( j >= 0  && temp.get(j) > key){
                    temp.set((j+1),temp.get(j));
                    j--;
                }
                temp.set((j+1),key);
            }
            //print
            for (Double aDouble : temp) {
                System.out.println(aDouble);
            }
            return temp.get((temp.size()-1)/2);
        }
        public Double getMode(ArrayList<Double> temp){
            int cout = 0;
            int newCout = 0;
            Double max = temp.get(0);
            Double newMax = temp.get(1);
            //get count of first element
            for(int i = 1; i < temp.size(); i++){
                if(max.equals( temp.get(i))){
                    cout++;
                }
            }
            System.out.println("curent max "+max+ " with cout of "+ cout);
            //count element 2
            for(int i = 0; i < temp.size(); i++){
                max = temp.get(i);
                for (Double aDouble : temp) {
                    if (max.equals(aDouble)) {
                        newCout++;
                        System.out.println("new max " + aDouble + " with cout of " + newCout);
                    }
                }
                if(newCout >=	 cout){
                    cout = newCout;
                    newMax = temp.get(i);
                }
                newCout = 0;
            }

            System.out.println("new max "+newMax+ " with cout of "+ cout);

            return newMax;
        }
    }
    //displays a graphical representation of distribution stats
    private class DisplayGraphHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            //DO ANALYSIS
            //analysisBox.getChildren().clear();
            String zero = "";
            String one = "";
            String two = "";
            String three = "";
            String four = "";
            String five = "";
            String six = "";
            String seven = "";
            String eight = "";
            String nine = "";
            String ten = "";

            for (Double datum : data) {
                if (datum < 10)
                    zero += "+";
                else if (datum < 20)
                    two += "+";
                else if (datum < 30)
                    three += "+";
                else if (datum < 40)
                    four += "+";
                else if (datum < 50)
                    five += "+";
                else if (datum < 60)
                    six += "+";
                else if (datum < 70)
                    seven += "+";
                else if (datum < 80)
                    eight += "+";
                else if (datum < 90)
                    nine += "+";
                else
                    ten += "+";


            }
            Label barrier = new Label("------Display graph------");
            grp09 = new Label("0%-9%     "+zero);
            grp1019 = new Label("10%-19% "+two);
            grp2029 = new Label("20%-29% "+three);
            grp3039 = new Label("30%-39% "+four);
            grp4049 = new Label("40%-49% "+five);
            grp5059 = new Label("50%-59% "+six);
            grp6069 = new Label("60%-69% "+seven);
            grp7079 = new Label("70%-79% "+eight);
            grp8089 = new Label("80%-89% "+nine);
            grp9099 = new Label("90%-100%"+ten);
            VBox holder = new VBox();
            holder.getChildren().addAll(barrier,grp09,grp1019,grp2029,grp3039,grp4049,grp5059,grp6069,grp7079,grp8089,grp9099);


            VBox panel = new VBox(holder);
            Scene errScene= new Scene(panel);
            Stage errStage = new Stage();
            errStage.setScene(errScene);
            errStage.show();
            //	chartBox.getChildren().addAll(holder);

        }

    }
    //delete data from core set redraw display area
    private class SetBoundariesHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            //get new bounds
            Stage boundStage = new Stage();
            Label lowLabel = new Label("low");
            Label highLabel = new Label("high");
            TextField lowField = new TextField("");
            TextField highField = new TextField("");
            Button change = new Button("Change Boundaries");
            VBox panel = new VBox(lowLabel,lowField,highLabel,highField,change);
            Scene bScene = new Scene(panel);
            //put an anonymous handler here to alter high/low
            change.setOnAction(new  EventHandler<ActionEvent>(){
                public void handle(ActionEvent event){
                    //get new bounds
                    try{
                        localLow = Double.parseDouble(lowField.getText());
                        localHigh = Double.parseDouble(highField.getText());
                        update(localLow,localHigh);
                        boundStage.close();
                        System.out.print("Setting new bounds");
                    }
                    catch(Exception e){
                        System.out.println("High and low boundaries must be be real numbers");
                        Alert a = new Alert(Alert.AlertType.ERROR);
                        a.setHeaderText("Set boundary error");
                        a.setContentText("High and low must be real numbers");
                        a.show();
                    }

                }
            });
            boundStage.setScene(bScene);
            boundStage.show();
        }

        public void update(double l, double h){
            low = l;
            high = h;

            if(data.size() == 0)
                return;

            //traverse core data set, remove
            for(int i = 0; i < data.size();i++){//jorms
                if(data.get(i) < low || data.get(i) > high){
                    data.remove(i);
                    i = 0;
                }
            }
            if(data.get(data.size()-1) < low || data.get(data.size()-1) > high){
                data.remove(data.size()-1);
            }
            displayBox.getChildren().clear();
            Label title = new Label("----Display Area----");
            displayBox.getChildren().add(title);
            labels.clear();
            int columns = (data.size()/4)+1;
            int col1 = columns;
            int col2 = col1+columns;
            int col3 = col2+columns;
            VBox column1 = new VBox();
            VBox column2 = new VBox();
            VBox column3 = new VBox();
            VBox column4 = new VBox();
            for(int i = 0; i < data.size();i++){//jorms
                Label ormsby = new Label(" "+data.get(i)+" ");
                labels.add(ormsby);
                if(i < col1){
                    column1.getChildren().add(labels.get(i));
                }
                else if(i < col2){
                    column2.getChildren().add(labels.get(i));
                }
                else if(i < col3){
                    column3.getChildren().add(labels.get(i));
                }
                else{
                    column4.getChildren().add(labels.get(i));
                }
            }

            HBox colContainer = new HBox(column1,column2,column3,column4);
            displayBox.getChildren().add(colContainer);

            //calculate mean
            double sum = 0;
            for (Double aDouble : data) {
                sum += aDouble;
            }
            mean = sum/data.size();
            meanLabel = new Label("mean: "+mean);
            median = getMedian(data);
            medianLabel = new Label("median: "+median);
            //calculate mode
            if(data.size() >2)
                mode = getMode(data);
            if(data.size() ==1)
                mode = data.get(0);
            if(data.size() ==0)
                mode = 0;
            modeLabel = new Label("mode: "+mode);

            //DO ANALYSIS
            analysisBox.getChildren().clear();
            int zero = 0;
            int one = 0;
            int two = 0;
            int three = 0;
            int four = 0;
            int five = 0;
            int six = 0;
            int seven = 0;
            int eight = 0;
            int nine = 0;
            int ten = 0;


            for (Double datum : data) {
                double num = datum / high;
                if (num < .10d)
                    zero++;
                else if (num < .20d)
                    two++;
                else if (num < .30d)
                    three++;
                else if (num < .40d)
                    four++;
                else if (num < .50d)
                    five++;
                else if (num < .60d)
                    six++;
                else if (num < .70d)
                    seven++;
                else if (num < .80d)
                    eight++;
                else if (num < .90d)
                    nine++;
                else
                    ten++;

            }
            grp09 = new Label("0%-9%"+zero);
            grp1019 = new Label("10%-19%:"+two);
            grp2029 = new Label("20%-29%:"+three);
            grp3039 = new Label("30%-39%:"+four);
            grp4049 = new Label("40%-49%:"+five);
            grp5059 = new Label("50%-59%:"+six);
            grp6069 = new Label("60%-69%:"+seven);
            grp7079 = new Label("70%-79%:"+eight);
            grp8089 = new Label("80%-89%:"+nine);
            grp9099 = new Label("90%-100%:"+ten);
            distributionBox.getChildren().clear();
            Label dist = new Label("----Distribution Area----");
            distributionBox.getChildren().addAll(dist,grp09,grp1019,grp2029,grp3039,grp4049,grp5059,grp6069,grp7079,grp8089,grp9099);//lower right
            //FINISH ANALYSIS

            Label analyze = new Label("----Analysis Area----");
            analysisBox.getChildren().add(analyze);
            analysisBox.getChildren().addAll(meanLabel,medianLabel,modeLabel);
        }

        public Double getMedian(ArrayList<Double> temp){
            //insertion sort
            for(int i = 1; i < temp.size(); i++){
                Double key = temp.get(i);
                int j = i -1;
                while( j >= 0  && temp.get(j) > key){
                    temp.set((j+1),temp.get(j));
                    j--;
                }
                temp.set((j+1),key);
            }
            //print
            for (Double aDouble : temp) {
                System.out.println(aDouble);
            }
            return temp.get((temp.size()-1)/2);
        }

        public Double getMode(ArrayList<Double> temp){
            int cout = 0;
            int newCout = 0;
            Double max = temp.get(0);
            Double newMax = temp.get(1);
            //get count of first element
            for(int i = 1; i < temp.size(); i++){
                if(max.equals( temp.get(i))){
                    cout++;
                }
            }
            System.out.println("curent max "+max+ " with cout of "+ cout);
            //count element 2
            for(int i = 1; i < temp.size(); i++){
                max = temp.get(i);
                for(int j = 1; j < temp.size(); j++){
                    if(max.equals( temp.get(j))){
                        newCout++;
                        System.out.println("new max "+ temp.get(j)+ " with cout of "+ newCout);
                    }
                }
                if(newCout > cout){
                    cout = newCout;
                    newMax = temp.get(i);
                }
                newCout = 0;
            }

            System.out.println("new max "+newMax+ " with cout of "+ newCout);

            return newMax;
        }


    }
    //dump display/analytics/distributions to .txt file
    private class CreateReportHandler implements EventHandler<ActionEvent>{
        public void handle(ActionEvent event){
            try{
                //object writer leads to garbage text(needs to be deserialized) using filewriter instead
                //file/out stream we are writing to
                FileOutputStream reportFile = new FileOutputStream("myReport.txt");
                // BUFFER
                PrintWriter outStream = new PrintWriter(reportFile);
                //write area
                outStream.write("Report of interesting numbers " + "\n");
                outStream.write("--------------------------------------" + "\n");
                outStream.write("Mean: " + mean + "\n");
                outStream.write("Median: " + median + "\n");
                outStream.write("Mode: " + mode + "\n");
                //stream contents need to be pushed to file from stream, could
                //avoid using this by doing a nonbuffered stream
                outStream.flush();

            }
            catch(Exception e){
                System.out.println("none");
            }
        }
    }

}