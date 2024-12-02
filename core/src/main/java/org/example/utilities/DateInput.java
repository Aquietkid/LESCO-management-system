package org.example.utilities;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class DateInput extends JPanel {

    private SpinnerDateModel dateModel;

    public DateInput() {
        init();
    }

    private void init() {
        setLayout(new FlowLayout());

        dateModel = getSpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(editor);

        add(dateSpinner);
        setVisible(true);
    }

    private SpinnerDateModel getSpinnerDateModel() {

        LocalDate localDate = LocalDate.now();

        Calendar calendar = Calendar.getInstance();

        calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth()); // Start date: As of current date at time of execution
        Date initialDate = calendar.getTime();
        Date startDate = calendar.getTime();

        localDate = localDate.plusYears(20);

        calendar.set(localDate.getYear(), Calendar.DECEMBER, 31); // End date: December 31, 20 years after current year
        Date endDate = calendar.getTime();

        return new SpinnerDateModel(initialDate, startDate, endDate, Calendar.DAY_OF_MONTH);
    }

    public int getDate() {
        return dateModel.getDate().getDate();
    }

    public int getMonth() {
        return dateModel.getDate().getMonth();
    }

    public int getYear() {
        return dateModel.getDate().getYear();
    }
}
