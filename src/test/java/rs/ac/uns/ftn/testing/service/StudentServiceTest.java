	package rs.ac.uns.ftn.testing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_COUNT_WITH_LAST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_FIRST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_ID;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_ID_TO_DELETE;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_INDEX;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_LASTNAME_COUNT;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.DB_LAST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.NEW_FIRST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.NEW_ID;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.NEW_INDEX;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.NEW_LAST_NAME;
import static rs.ac.uns.ftn.testing.constants.StudentConstants.PAGE_SIZE;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import rs.ac.uns.ftn.testing.model.Student;
import rs.ac.uns.ftn.testing.repository.StudentRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentServiceTest {
	
	/*
	Mocking mehanizam omogućuje simulaciju ponašanja objekata koje testirani objekat koristi
	Da bi se testirani objekat testirao u izolaciji važno je da referencirani objekti ne unose
	grešku. Potrebno je simulirati da referencirani objekti uvek rade ispravno.
	Da bismo servise testirali u izolaciji (a znamo da servisi koriste metode repozitorijuma)mokujemo repozitorijume
	dodavanjem anotacije @Mock.
	 */
	@Mock
	private StudentRepository studentRepositoryMock;
	
	@Mock
	private Student studentMock;
	
	/*
	Kako servis koristi metode repozitorijuma koji smo mokovali, moramo taj mokovani repozitorijum injektovati
	dodavanjem anotacije @InjectMocks.
	 */
	@InjectMocks
	private StudentService studentService;

/*
	Anotacija @Test naznačava Springu da će se anotirana metoda izvrši prilikom testiranja.
	Ukoliko se ona izostavi, test metoda se neće izvršiti.
*/
	@Test
	public void testFindAll() {
	
		/*
		Kako za testove koristimo mokovane repository objekte moramo da definišemo šta će se desiti kada se
		pozove određena metoda kombinacijom "when"-"then" Mockito metoda.
		 */
		when(studentRepositoryMock.findAll()).thenReturn(Arrays.asList(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME)));
		List<Student> students = studentService.findAll();
		assertThat(students).hasSize(1);
		
		/*
		Možemo verifikovati ponašanje mokovanih objekata pozivanjem verify* metoda.
		 */
		verify(studentRepositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	@Test
	public void testFindByFirstLetters() {
	
		when(studentRepositoryMock.findAll()).thenReturn(Arrays.asList(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME), new Student(NEW_ID, NEW_INDEX, NEW_FIRST_NAME, NEW_LAST_NAME)));
		List<Student> students = studentService.findByFirstLetters("p");
		assertThat(students).hasSize(1);
		assertEquals("Pera", students.get(0).getFirstName());
		verify(studentRepositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	@Test
	public void testFindAllPageable() {
		
		PageRequest pageRequest = new PageRequest(1, PAGE_SIZE); //second page
		when(studentRepositoryMock.findAll(pageRequest)).thenReturn(new PageImpl<Student>(Arrays.asList(new Student(DB_ID, NEW_INDEX, NEW_FIRST_NAME, NEW_LAST_NAME)).subList(0, 1), pageRequest, 1));
		Page<Student> students = studentService.findAll(pageRequest);
		assertThat(students).hasSize(1);
		verify(studentRepositoryMock, times(1)).findAll(pageRequest);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	@Test 
	public void testFindOne() {
		
		when(studentRepositoryMock.findOne(DB_ID)).thenReturn(studentMock);
		Student dbStudent = studentService.findOne(DB_ID);
		assertEquals(studentMock, dbStudent);
        verify(studentRepositoryMock, times(1)).findOne(DB_ID);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
/*	
	Test metode ne vrše trajne izmene nad bazom podataka.
	Test metode anotirane sa @Transactional:
		- za svaku test metodu se pokreće nova transakcija.
		- transakcija se automatski poništava (rollback) na kraju metode. Izmene nad bazom
		  se poništavaju nakon metode iako je rollback podrazumevano ponašanje,
		  može da se stavi i anotacija @Rollback(true) da bi bilo vidljivije.
*/
	@Test
    @Transactional
    @Rollback(true) //it can be omitted because it is true by default
	public void testAdd() {
		
		when(studentRepositoryMock.findAll()).thenReturn(Arrays.asList(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME)));
		Student student = new Student();
		student.setFirstName(NEW_FIRST_NAME);
		student.setLastName(NEW_LAST_NAME);
		student.setIndex(NEW_INDEX);
		
		when(studentRepositoryMock.save(student)).thenReturn(student);
		
		int dbSizeBeforeAdd = studentService.findAll().size();
		
		Student dbStudent = studentService.save(student);
		assertThat(dbStudent).isNotNull();
		
		when(studentRepositoryMock.findAll()).thenReturn(Arrays.asList(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME), student));
		// Validate that new student is in the database
        List<Student> students = studentService.findAll();
        assertThat(students).hasSize(dbSizeBeforeAdd + 1);
        dbStudent = students.get(students.size() - 1); //get last student
        assertThat(dbStudent.getFirstName()).isEqualTo(NEW_FIRST_NAME);
        assertThat(dbStudent.getLastName()).isEqualTo(NEW_LAST_NAME);
        assertThat(dbStudent.getIndex()).isEqualTo(NEW_INDEX);
        verify(studentRepositoryMock, times(2)).findAll();
        verify(studentRepositoryMock, times(1)).save(student);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	@Test
    @Transactional
	public void testAddv2() {
		
		// Definisanje ponašanja       
        when(studentRepositoryMock.save(studentMock)).thenReturn(studentMock);
        // Akcija         
        Student savedStudent = studentService.save(studentMock);
        // Assert         
        assertThat(savedStudent, is(equalTo(studentMock)));
	}
	
	@Test
    @Transactional
    @Rollback(true)
	public void testUpdate() {
		
		when(studentRepositoryMock.findOne(DB_ID)).thenReturn(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME));
		Student dbStudent = studentService.findOne(DB_ID);
		
		dbStudent.setFirstName(NEW_FIRST_NAME);
		dbStudent.setLastName(NEW_LAST_NAME);
		dbStudent.setIndex(NEW_INDEX);
		
		when(studentRepositoryMock.save(dbStudent)).thenReturn(dbStudent);
		dbStudent = studentService.save(dbStudent);
		assertThat(dbStudent).isNotNull();
		
		//verify that database contains updated data
		dbStudent = studentService.findOne(DB_ID);
        assertThat(dbStudent.getFirstName()).isEqualTo(NEW_FIRST_NAME);
        assertThat(dbStudent.getLastName()).isEqualTo(NEW_LAST_NAME);
        assertThat(dbStudent.getIndex()).isEqualTo(NEW_INDEX);
        verify(studentRepositoryMock, times(2)).findOne(DB_ID);
        verify(studentRepositoryMock, times(1)).save(dbStudent);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testRemove() {
		
		when(studentRepositoryMock.findAll()).thenReturn(Arrays.asList(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME), new Student(NEW_ID, NEW_INDEX, NEW_FIRST_NAME, NEW_LAST_NAME)));
		int dbSizeBeforeRemove = studentService.findAll().size();
		doNothing().when(studentRepositoryMock).delete(DB_ID_TO_DELETE);
		studentService.remove(DB_ID_TO_DELETE);
		
		when(studentRepositoryMock.findAll()).thenReturn(Arrays.asList(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME)));
		List<Student> students = studentService.findAll();
		assertThat(students).hasSize(dbSizeBeforeRemove - 1);
		
		when(studentRepositoryMock.findOne(DB_ID_TO_DELETE)).thenReturn(null);
		Student dbStudent = studentService.findOne(DB_ID_TO_DELETE);
		assertThat(dbStudent).isNull();
		verify(studentRepositoryMock, times(1)).delete(DB_ID_TO_DELETE);
		verify(studentRepositoryMock, times(2)).findAll();
        verify(studentRepositoryMock, times(1)).findOne(DB_ID_TO_DELETE);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	@Test
	public void testFindByIndex() {
		
		when(studentRepositoryMock.findOneByIndex(DB_INDEX)).thenReturn(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME));
		Student dbStudent = studentService.findByIndex(DB_INDEX);
		assertThat(dbStudent).isNotNull();
		
		assertThat(dbStudent.getId()).isEqualTo(1L);
		assertThat(dbStudent.getFirstName()).isEqualTo(DB_FIRST_NAME);
        assertThat(dbStudent.getLastName()).isEqualTo(DB_LAST_NAME);
        assertThat(dbStudent.getIndex()).isEqualTo(DB_INDEX);
        verify(studentRepositoryMock, times(1)).findOneByIndex(DB_INDEX);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	@Test
	public void testFindByLastName() {
		when(studentRepositoryMock.findAllByLastName(DB_LAST_NAME)).thenReturn(Arrays.asList(new Student(DB_ID, DB_INDEX, DB_FIRST_NAME, DB_LAST_NAME), new Student(2L, "ra12-2014", "Milica", DB_LAST_NAME)));
		List<Student> students = studentService.findByLastName(DB_LASTNAME_COUNT);
		assertThat(students).hasSize(DB_COUNT_WITH_LAST_NAME);
		verify(studentRepositoryMock, times(1)).findAllByLastName(DB_LAST_NAME);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
	/*
	 * Negative tests
	 */
	
/*	
	Test može da definiše da očekuje da se pri pozivu metode desi određeni izuzetak.
	Test prolazi ako se takav izuzetak desi. Ovo se koristi najčešće za negativne testove 
	kada verifikujemo ponašanje na nevalidne ulaze.
*/
	@Test(expected = DataIntegrityViolationException.class)
    @Transactional
    @Rollback(true)
	public void testAddNonUniqueIndex() {
		
		Student student = new Student();
		student.setFirstName(NEW_FIRST_NAME);
		student.setLastName(NEW_LAST_NAME);
		student.setIndex(DB_INDEX); //existing index
		
		when(studentRepositoryMock.save(student)).thenThrow(DataIntegrityViolationException.class);
		studentService.save(student);
		verify(studentRepositoryMock, times(1)).save(student);
        verifyNoMoreInteractions(studentRepositoryMock);
		
	}
	
	@Test(expected = DataIntegrityViolationException.class)
	@Transactional
	@Rollback(true)
	public void testAddNullIndex() {
		
		Student student = new Student();
		student.setFirstName(NEW_FIRST_NAME);
		student.setLastName(NEW_LAST_NAME);
		
		when(studentRepositoryMock.save(student)).thenThrow(DataIntegrityViolationException.class);
		studentService.save(student);
		verify(studentRepositoryMock, times(1)).save(student);
        verifyNoMoreInteractions(studentRepositoryMock);
	}
	
}
