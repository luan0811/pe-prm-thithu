package com.example.pethithu;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.ImageButton;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.AdapterView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pethithu.Model.Major;
import com.example.pethithu.Model.Student;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient client = new OkHttpClient();
    private List<Major> majorList = new ArrayList<>();
    private List<Student> studentList = new ArrayList<>();
    private ListView listViewStudents;
    private StudentAdapter studentAdapter;
    private Button buttonAddMajor;
    private Button buttonAddStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewStudents = findViewById(R.id.listViewStudents);
        buttonAddMajor = findViewById(R.id.buttonAddMajor);
        buttonAddStudent = findViewById(R.id.buttonAddStudent);

        buttonAddMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMajorDialog();
            }
        });

        buttonAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddStudentDialog();
            }
        });

        // Gọi API để lấy dữ liệu
        fetchMajors();
    }

    // Gọi API để lấy danh sách Majors
    private void fetchMajors() {
        Request request = new Request.Builder()
                .url("https://670e826c3e7151861654d60d.mockapi.io/Major")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API_ERROR", "Error fetching majors", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    Type majorListType = new TypeToken<ArrayList<Major>>(){}.getType();
                    majorList = gson.fromJson(responseData, majorListType);

                    runOnUiThread(() -> {
                        // Log để debug
                        Log.d("Majors", "Fetched " + majorList.size() + " majors");
                    });

                    // Khi có dữ liệu Majors, gọi API lấy Students
                    fetchStudents();
                }
            }
        });
    }

    // Gọi API để lấy danh sách Students
    private void fetchStudents() {
        Request request = new Request.Builder()
                .url("https://670e826c3e7151861654d60d.mockapi.io/Student")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API_ERROR", "Error fetching students", e);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Type listType = new TypeToken<ArrayList<Student>>(){}.getType();
                    studentList = new Gson().fromJson(responseBody, listType);

                    // Sau khi có cả Majors và Students, gộp dữ liệu
                    joinData();
                }
            }
        });
    }

    // Gộp nameMajor vào student dựa trên MajorId
    private void joinData() {
        // Tạo một Map để lưu trữ các ngành học theo ID
        Map<String, String> majorMap = new HashMap<>();
        for (Major major : majorList) {
            majorMap.put(major.getId(), major.getNameMajor());
        }

        // Gán nameMajor cho mỗi sinh viên
        for (Student student : studentList) {
            String majorId = student.getMajorId();
            Log.d("JoinData", "Student: " + student.getName() + ", Major: " + student.getNameMajor());
            if (majorId != null) {
                String nameMajor = majorMap.get(majorId);
                if (nameMajor != null) {
                    student.setNameMajor(nameMajor);
                    student.setNameMajor(nameMajor);
                } else {
                    // Xử lý trường hợp không tìm thấy ngành học
                    student.setNameMajor("Unknown Major");
                }
            } else {
                // Xử lý trường hợp sinh viên không có MajorId
                student.setNameMajor("No Major Assigned");
            }
        }

        // Cập nhật adapter với dữ liệu student
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                studentAdapter = new StudentAdapter(MainActivity.this, studentList);
                listViewStudents.setAdapter(studentAdapter);
            }
        });
    }

    // Show dialog to add a new Major
    private void showAddMajorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Major");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String majorName = input.getText().toString().trim();
                if (!majorName.isEmpty()) {
                    addMajor(majorName);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a major name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Add new method to add a Major
    private void addMajor(String majorName) {
        Major newMajor = new Major();
        newMajor.setNameMajor(majorName);

        Gson gson = new Gson();
        String json = gson.toJson(newMajor);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url("https://670e826c3e7151861654d60d.mockapi.io/Major")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Error adding major", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Major added successfully", Toast.LENGTH_SHORT).show();
                            fetchMajors(); // Refresh the major list
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Failed to add major", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void showAddStudentDialog() {
        // Kiểm tra xem có major nào không
        if (majorList == null || majorList.isEmpty()) {
            Toast.makeText(this, "Please add some majors first", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Student");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_student, null);
        builder.setView(view);

        final EditText editTextName = view.findViewById(R.id.editTextName);
        final EditText editTextDate = view.findViewById(R.id.editTextDate);
        final EditText editTextAddress = view.findViewById(R.id.editTextAddress);
        final RadioGroup radioGroupGender = view.findViewById(R.id.radioGroupGender);
        final Spinner spinnerMajor = view.findViewById(R.id.spinnerMajor);
        final ImageButton btnDatePicker = view.findViewById(R.id.btnDatePicker);

        // Set up date picker
        editTextDate.setOnClickListener(v -> showDatePickerDialog(editTextDate));
        btnDatePicker.setOnClickListener(v -> showDatePickerDialog(editTextDate));

        // Set up the spinner with major names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        // Add majors to adapter
        for (Major major : majorList) {
            adapter.add(major.getNameMajor());
        }
        
        spinnerMajor.setAdapter(adapter);
        adapter.notifyDataSetChanged(); // Thông báo adapter cập nhật dữ liệu

        // Thêm listener cho Spinner để kiểm tra
        spinnerMajor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMajor = parent.getItemAtPosition(position).toString();
                // Log để debug
                Log.d("Spinner", "Selected major: " + selectedMajor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editTextName.getText().toString().trim();
                String date = editTextDate.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();
                
                // Get selected gender
                int selectedId = radioGroupGender.getCheckedRadioButtonId();
                RadioButton radioButton = view.findViewById(selectedId);
                String gender = radioButton.getText().toString();
                
                String selectedMajor = spinnerMajor.getSelectedItem().toString();

                if (!name.isEmpty() && !date.isEmpty() && !address.isEmpty()) {
                    String majorId = getMajorIdByName(selectedMajor);
                    addStudent(name, date, gender, address, majorId);
                } else {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private String getMajorIdByName(String majorName) {
        for (Major major : majorList) {
            if (major.getNameMajor().equals(majorName)) {
                return major.getId();
            }
        }
        return null;
    }

    private void addStudent(String name, String date, String gender, String address, String majorId) {
        Student newStudent = new Student();
        newStudent.setName(name);
        newStudent.setDate(date);
        newStudent.setGender(gender);
        newStudent.setAddress(address);
        newStudent.setMajorId(majorId);

        Gson gson = new Gson();
        String json = gson.toJson(newStudent);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url("https://670e826c3e7151861654d60d.mockapi.io/Student")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Error adding student", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Student added successfully", Toast.LENGTH_SHORT).show();
                            fetchStudents(); // Refresh the student list
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Failed to add student", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    // Add this method to show the DatePickerDialog
    private void showDatePickerDialog(final EditText editTextDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Format the date as needed
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", 
                            dayOfMonth, monthOfYear + 1, year);
                        editTextDate.setText(selectedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
}
