package com.sap.bulletinboard.ads.models;

import com.sap.bulletinboard.ads.config.EmbeddedDatabaseConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EmbeddedDatabaseConfig.class})
public class AdvertisementRepositoryTest {

    private static final String SOME_TITLE = "MyNewAdvertisement";
    private static final String SOME_NEW_TITLE = "MyNewAdvertisement - New title!";

    @Autowired
    private AdvertisementRepository repository;

    @Test
    public void setIdOnFirstSave() {
        Advertisement advertisement = saveAd(new Advertisement(), SOME_TITLE);
        assertThat(advertisement.getId(), is(notNullValue()));
    }

    @Test
    public void setCreatedAtOnFirstSaveOnly() throws InterruptedException {
        Advertisement advertisement = saveAd(new Advertisement(), SOME_TITLE);

        Timestamp createdAt = advertisement.getCreatedAt();
        assertThat(createdAt, is(notNullValue()));

        Thread.sleep(10);
        advertisement = saveAd(advertisement, SOME_NEW_TITLE);

        Timestamp createdAtAfterSave = advertisement.getCreatedAt();
        assertThat(createdAt, is(createdAtAfterSave));
    }

    @Test
    public void setUpdatedAtOnEverySave() throws InterruptedException {
        Advertisement advertisement = saveAd(new Advertisement(), SOME_TITLE);
        advertisement = saveAd(advertisement, SOME_NEW_TITLE);

        Timestamp updatedAt = advertisement.getUpdatedAt();
        assertThat(updatedAt, is(notNullValue()));

        Thread.sleep(10);
        advertisement = saveAd(advertisement, SOME_TITLE);

        Timestamp updatedAtAfterSave = advertisement.getUpdatedAt();
        assertThat(updatedAt, is(not(updatedAtAfterSave)));
    }

    @Test(expected = JpaOptimisticLockingFailureException.class)
    public void useVersionForConflicts() {
        Advertisement advertisement = new Advertisement();
        advertisement.setTitle(SOME_TITLE);
        advertisement = repository.save(advertisement);

        advertisement.setTitle(SOME_NEW_TITLE);
        repository.save(advertisement);

        repository.save(advertisement);
    }

    private Advertisement saveAd(Advertisement advertisement, String title) {
        advertisement.setTitle(title);
        return repository.save(advertisement);
    }
}
