package lotto.controller;

import java.util.Map;
import lotto.controller.dto.LottoResult;
import lotto.controller.dto.WinningResult;
import lotto.model.Lotto;
import lotto.model.Lottos;
import lotto.model.Money;
import lotto.model.Number;
import lotto.model.Rank;
import lotto.model.WinningLotto;
import lotto.model.WinningResultCalculator;
import lotto.util.NumberGenerator;
import lotto.util.RandomNumberGenerator;
import lotto.view.InputView;
import lotto.view.OutputView;

public class LottoController {

    private final InputView inputView;
    private final OutputView outputView;

    public LottoController(final InputView inputView, final OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void start() {
        Money money = getMoney();
        Lottos lottos = buyLottos(money, new RandomNumberGenerator());
        WinningLotto winningLotto = getWinningLotto();
        calculateLottoResult(lottos, winningLotto, money);
    }

    private Money getMoney() {
        while (true) {
            try {
                outputView.printPurchaseMessage();
                String input = inputView.readInput();
                return Money.from(input);
            } catch (IllegalArgumentException e) {
                outputView.printError(e);
            }
        }
    }

    private Lottos buyLottos(final Money money, final NumberGenerator numberGenerator) {
        long buyCount = money.buyLotto();
        outputView.printBuyLotto(buyCount);
        Lottos lottos = Lottos.of(buyCount, numberGenerator);
        printLottos(lottos);
        return lottos;
    }

    private void printLottos(final Lottos lottos) {
        for (final Lotto lotto : lottos.getLottos()) {
            outputView.printLotto(LottoResult.from(lotto.getLottoNumbers()));
        }
    }

    private WinningLotto getWinningLotto() {
        outputView.printWinningNumbers();
        Lotto lotto = generateWinningLottoNumbers();
        outputView.printBonusNumber();
        Number bonusNumber = generateBonusNumber();
        return new WinningLotto(lotto, bonusNumber);
    }

    private Lotto generateWinningLottoNumbers() {
        while (true) {
            try {
                String input = inputView.readInput();
                return Lotto.fromInput(input);
            } catch (IllegalArgumentException e) {
                outputView.printError(e);
            }
        }
    }

    private Number generateBonusNumber() {
        while (true) {
            try {
                String input = inputView.readInput();
                return Number.from(input);
            } catch (IllegalArgumentException e) {
                outputView.printError(e);
            }
        }
    }

    private void calculateLottoResult(final Lottos lottos, final WinningLotto winningLotto, final Money money) {
        WinningResultCalculator winningResultCalculator = new WinningResultCalculator();
        Map<Rank, Integer> winningStatus = winningResultCalculator.calculateWinningStatus(winningLotto, lottos);
        double revenue = winningResultCalculator.getReturnRate(winningStatus, money);
        printResult(winningStatus, revenue);
    }

    private void printResult(final Map<Rank, Integer> status, final double revenue) {
        outputView.printResult(WinningResult.from(status), revenue);
    }
}
