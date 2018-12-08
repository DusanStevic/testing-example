package rs.ac.uns.ftn.testing.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.testing.model.Student;
import rs.ac.uns.ftn.testing.repository.StudentRepository;

@Service
public class StudentService {
	
	@Autowired
	StudentRepository studentRepository;
	
	public Student findOne(Long id) {
		return studentRepository.findOne(id);
	}

	public List<Student> findAll() {
		return studentRepository.findAll();
	}
	
	public Page<Student> findAll(Pageable page) {
		return studentRepository.findAll(page);
	}

	public Student save(Student student) {
		return studentRepository.save(student);
	}

	public void remove(Long id) {
		studentRepository.delete(id);
	}
	
	public Student findByIndex(String index) {
		return studentRepository.findOneByIndex(index);
	}
	
	public List<Student> findByLastName(String lastName) {
		return studentRepository.findAllByLastName(lastName);
	}
	
	public List<Student> findByFirstLetters(String letters) {
		List<Student> students = studentRepository.findAll();
		List<Student> resultList = new ArrayList<>();
		for(Student s : students) {
			if(s.getFirstName().toLowerCase().startsWith(letters.toLowerCase())) {
				resultList.add(s);
			}
		}
		return resultList;
	}
	
}
