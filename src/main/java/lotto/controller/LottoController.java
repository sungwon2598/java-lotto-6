package lotto.controller;

import lotto.domain.BonusNumber;
import lotto.domain.Lotto;
import lotto.domain.LottoFactory;
import lotto.domain.ProfitMeter;
import lotto.domain.PurchaseProcessor;
import lotto.domain.WinnerCalculator;
import lotto.domain.WinningNumbers;
import lotto.util.NumberConverter;
import lotto.util.ResultFormatter;
import lotto.view.InputView;
import lotto.view.OutputView;

public class LottoController {
    private final NumberConverter numberConverter = new NumberConverter();
    private final InputView inputView = new InputView();
    private final OutputView outputView = new OutputView();
    private final ResultFormatter resultFormatter = new ResultFormatter();
    private LottoFactory lottoFactory;
    private WinnerCalculator winnerCalculator;
    private PurchaseProcessor purchaseProcessor;

    public void startLotto() {
        purchaseLottos();
        Lotto winnerNumbers = lottoDraw();
        BonusNumber bonusNumber = handleBonusNumber(winnerNumbers);
        profitCalculation(winnerNumbers, bonusNumber);
    }

    private void purchaseLottos() {
        handlePurchaseAmount();
        generateLottoNumbers();
    }

    private Lotto lottoDraw() {
        return handleWinningNumbers();
    }

    private void profitCalculation(Lotto winnerNumbers, BonusNumber bonusNumber) {
        checkLottoResults(winnerNumbers, bonusNumber);
        computeLottoProfitability();
    }

    private void handlePurchaseAmount() {
        while (true) {
            try {
                this.purchaseProcessor = new PurchaseProcessor(inputView.requestPurchaseAmount());
                outputView.enterLine();
                break;
            } catch (IllegalArgumentException e) {
                outputView.displayErrorMessage(e);
            }
        }
    }

    private void generateLottoNumbers() {
        this.lottoFactory = new LottoFactory(purchaseProcessor.getLottoCount());
        outputView.displayLottoCount(purchaseProcessor.getLottoCount());
        outputView.displayLottoReturns(lottoFactory.getTickets());
        outputView.enterLine();
    }

    private Lotto handleWinningNumbers() {
        while (true) {
            try {
                String lottoNumber = inputView.requestWinningNumbers();
                Lotto winnerNumbers = new Lotto(new WinningNumbers(numberConverter).process(lottoNumber));
                outputView.enterLine();
                return winnerNumbers;
            } catch (IllegalArgumentException e) {
                outputView.displayErrorMessage(e);
            }
        }
    }

    private BonusNumber handleBonusNumber(Lotto winnerNumbers) {
        while (true) {
            try {
                BonusNumber bonusNumber = new BonusNumber(winnerNumbers, numberConverter);
                bonusNumber.validateNumber(inputView.requestBonusNumbers());
                outputView.enterLine();
                return bonusNumber;
            } catch (IllegalArgumentException e) {
                outputView.displayErrorMessage(e);
            }
        }
    }

    private void checkLottoResults(Lotto winnerNumbers, BonusNumber bonusNumber) {
        this.winnerCalculator = new WinnerCalculator(lottoFactory, winnerNumbers, bonusNumber);
        outputView.displayWinningLotto(resultFormatter.createWinningStatistics(winnerCalculator.calculate()));
    }

    private void computeLottoProfitability() {
        ProfitMeter profitMeter = new ProfitMeter(winnerCalculator.getWinnerCount(), purchaseProcessor.getLottoCount());
        outputView.displayRateOfReturn(profitMeter.calculateYield());
    }
}




