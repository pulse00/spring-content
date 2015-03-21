package org.springframework.content.examples;


import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.content.examples.Claim;
import org.springframework.content.examples.ClaimForm;
import org.springframework.content.examples.ClaimFormStore;
import org.springframework.content.examples.ClaimRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = org.springframework.content.examples.Application.class)   
public class ClaimTests {

	@Autowired
	private ClaimRepository claimRepo;
	
	@Autowired 
	ClaimFormStore claimFormStore;

	@Before
	public void setUp() {
		
		// ensure auto-configuration has worked
		Assert.assertNotNull(claimRepo);
		Assert.assertNotNull(claimFormStore);
		
		// delete any existing claim forms
		List<Claim> existingClaims = claimRepo.findAll();
		for (Claim existingClaim : existingClaims) {
			claimFormStore.unsetContent(existingClaim.getClaimForm());
		}
		
		// delete existing claims
		claimRepo.deleteAll();
	}
	
	@Test
	public void test() throws IOException {
		Claim claim = new Claim();
		claim.setFirstName("John");
		claim.setLastName("Smith");
		
		claim.setClaimForm(new ClaimForm());
		claimFormStore.setContent(claim.getClaimForm(), this.getClass().getResourceAsStream("/claim_form.pdf"));
		Assert.assertThat(claim.getClaimForm().getContentId(), is(notNullValue()));
		Assert.assertThat(claim.getClaimForm().getContentId().trim().length(), greaterThan(0));
		Assert.assertEquals(claim.getClaimForm().getContentLength(), 1226609);
		
		claim = claimRepo.save(claim);

		Assert.assertTrue(IOUtils.contentEquals(this.getClass().getResourceAsStream("/claim_form.pdf"), claimFormStore.getContent(claim.getClaimForm())));
	}
}
