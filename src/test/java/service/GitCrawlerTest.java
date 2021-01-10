package service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.tallison.gitcrawler.model.ExtensionData;
import com.tallison.gitcrawler.service.GitCrawlerService;

public class GitCrawlerTest {
	
	GitCrawlerService service = new GitCrawlerService();
	
	@Test
	public void crawGithubTest() throws IOException, InterruptedException {
		
		List<ExtensionData> expectedResult = new ArrayList<ExtensionData>();
		expectedResult.add(new ExtensionData("py", 377, 11568.0));
		expectedResult.add(new ExtensionData("sqlite3", 0, 143360.0));

		List<ExtensionData> result = this.service.getRepository("https://github.com/tallison-cm/todo_list");
		
		assertEquals(result.toString(), expectedResult.toString());
	}

}
