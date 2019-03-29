package cf.dmms.app.core.book;

import cf.dmms.app.core.book.storage.BookContentStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.List;

@Service
@Transactional
public class BookService {

	private BookRepository bookRepository;
	private BookContentStore bookContentStore;

	public BookService(BookRepository bookRepository, BookContentStore bookContentStore) {
		this.bookRepository = bookRepository;
		this.bookContentStore = bookContentStore;
	}

	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}

	public Page<Book> getAllBooks(Pageable pageable) {
		return bookRepository.findAll(pageable);
	}

	public Book getBook(Long id) {
		return bookRepository.getOne(id);
	}

	public void deleteBookById(Long id) {
		bookRepository.deleteById(id);
	}

	public Book addBook(Book book) {
		return bookRepository.save(book);
	}

	public InputStream getBookContent(Book book, MediaType type) {
		Format format = book.getFormats().stream()
				.filter(f -> f.getFormat() == type)
				.findFirst()
				.orElseThrow(EntityNotFoundException::new);

		return bookContentStore.getContent(format);
	}

	public void addBookFormat(Long bookId, Format format, InputStream inputStream) {
		Book book = bookRepository.getOne(bookId);
		if (book.getFormats().contains(format)) {
			throw new BookFormatAlreadyExistsException();
		}

		bookContentStore.setContent(format, inputStream);
		book.getFormats().add(format);
	}
}
