package org.delcom.ifs25011pbo2026springbootcashflow;

import org.delcom.ifs25011pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25011pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.ifs25011pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

/**
 * Smoke test: memastikan wiring bean Clean Architecture berjalan
 * dan App.main mendelegasikan ke SpringApplication.run.
 */
@SpringBootTest
class AppTest {

    @Autowired
    private ITransactionRepository transactionRepository;

    @Autowired
    private FinanceUseCase financeUseCase;

    @Autowired
    private FinancePresenter financePresenter;

    @Test
    void contextLoads_andBeansAreWired() {
        assertNotNull(transactionRepository);
        assertNotNull(financeUseCase);
        assertNotNull(financePresenter);
    }

    @Test
    void main_delegatesToSpringApplicationRun() throws Exception {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            App.main(new String[]{});

            springApplication.verify(() -> SpringApplication.run(eq(App.class), any(String[].class)));
        }
    }
}
