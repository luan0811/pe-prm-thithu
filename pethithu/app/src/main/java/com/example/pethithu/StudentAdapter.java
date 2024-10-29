package com.example.pethithu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pethithu.Model.Student;

import java.util.List;

public class StudentAdapter extends BaseAdapter {

    private Context context;
    private List<Student> studentList;

    public StudentAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @Override
    public int getCount() {
        return studentList.size();
    }

    @Override
    public Object getItem(int position) {
        return studentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.student_item, parent, false);
        }

        // Lấy student hiện tại
        Student student = studentList.get(position);

        // Gán các giá trị vào view
        TextView studentName = convertView.findViewById(R.id.studentName);
        TextView studentMajor = convertView.findViewById(R.id.studentMajor);
        TextView studentAddress = convertView.findViewById(R.id.studentAddress);
        TextView studentGender = convertView.findViewById(R.id.studentGender);
        TextView studentDate = convertView.findViewById(R.id.studentDate);

        studentName.setText(student.getName());
        studentMajor.setText(student.getNameMajor());
        studentAddress.setText(student.getAddress());
        studentGender.setText(student.getGender());
        studentDate.setText(student.getDate());

        return convertView;
    }
}
