package nts.uk.ctx.at.aggregation.dom.schedulecounter.estimate;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;

@RunWith(JMockit.class)
public class EstimateAmountListTest {


	@Injectable EstimateAmountList.Require require;


	@Test
	public void getters() {
		val estimateAmount = EstimateAmountList.create(Arrays.asList(
				  new EstimateAmountByCondition(new EstimateAmountNo(1), new EstimateAmount(500))
				, new EstimateAmountByCondition(new EstimateAmountNo(2), new EstimateAmount(600))
				, new EstimateAmountByCondition(new EstimateAmountNo(3), new EstimateAmount(700))
			));
		NtsAssert.invokeGetters(estimateAmount);
	}


	/**
	 * input: 	目安金額リスト.size() ＞ 5
	 * output: Msg_1869
	 *
	 */
	@Test
	public void create_estimateAmounts_Msg_1869() {
		NtsAssert.businessException("Msg_1869", () -> {
			EstimateAmountList.create(Arrays.asList(
						  new EstimateAmountByCondition(new EstimateAmountNo(1), new EstimateAmount(500))
						, new EstimateAmountByCondition(new EstimateAmountNo(2), new EstimateAmount(600))
						, new EstimateAmountByCondition(new EstimateAmountNo(3), new EstimateAmount(700))
						, new EstimateAmountByCondition(new EstimateAmountNo(3), new EstimateAmount(700))
						, new EstimateAmountByCondition(new EstimateAmountNo(4), new EstimateAmount(700))
						, new EstimateAmountByCondition(new EstimateAmountNo(5), new EstimateAmount(800))
					)
			);
		});
	}

	/**
	 * input: 目安金額リストの位置番号が重複します
	 * output: Msg_1870
	 *
	 */
	@Test
	public void create_estimateAmounts_Msg_1870() {
		NtsAssert.businessException("Msg_1870", () -> {
			EstimateAmountList.create(Arrays.asList(
						  new EstimateAmountByCondition(new EstimateAmountNo(1), new EstimateAmount(500))
						, new EstimateAmountByCondition(new EstimateAmountNo(3), new EstimateAmount(600))
						, new EstimateAmountByCondition(new EstimateAmountNo(3), new EstimateAmount(600))
						, new EstimateAmountByCondition(new EstimateAmountNo(4), new EstimateAmount(600))
					));
		});
	}

	/**
	 * input: 目安金額枠NO:　2，3，4のリスト ->　←1から始まらない
	 * output: Msg_1871
	 *
	 */
	@Test
	public void create_estimateAmounts_startNot1_Msg_1871() {
		NtsAssert.businessException("Msg_1871", () -> {
			EstimateAmountList.create(Arrays.asList(
						  new EstimateAmountByCondition(new EstimateAmountNo(2), new EstimateAmount(500))
						, new EstimateAmountByCondition(new EstimateAmountNo(3), new EstimateAmount(600))
						, new EstimateAmountByCondition(new EstimateAmountNo(4), new EstimateAmount(700))
					));
		});
	}

	/**
	 * input: 目安金額枠NO:　1，3，4のリスト ->　←連続ない
	 * output: Msg_1871
	 *
	 */
	@Test
	public void create_estimateAmounts_Not_Continuous_Msg_1871() {
		NtsAssert.businessException("Msg_1871", () -> {
			EstimateAmountList.create(Arrays.asList(
						  new EstimateAmountByCondition(new EstimateAmountNo(1), new EstimateAmount(500))
						, new EstimateAmountByCondition(new EstimateAmountNo(3), new EstimateAmount(600))
						, new EstimateAmountByCondition(new EstimateAmountNo(4), new EstimateAmount(700))
					));
		});
	}

	/**
	 * input: 目安金額リスト[N].金額 = 目安金額リスト[N+1].金額
	 * output: Msg_147
	 *
	 */
	@Test
	public void create_equal_amount_Msg_147() {
		NtsAssert.businessException("Msg_147", () -> {
			EstimateAmountList.create(Arrays.asList(
						  new EstimateAmountByCondition(new EstimateAmountNo(1), new EstimateAmount(500))
						, new EstimateAmountByCondition(new EstimateAmountNo(2), new EstimateAmount(500))
					));
		});
	}

	/**
	 * input: 目安金額リスト[N].金額 > 目安金額リスト[N+1].金額
	 * output: Msg_147
	 *
	 */
	@Test
	public void create_more_amount_Msg_147() {
		NtsAssert.businessException("Msg_147", () -> {
			EstimateAmountList.create(Arrays.asList(
						  new EstimateAmountByCondition(new EstimateAmountNo(1), new EstimateAmount(500))
						, new EstimateAmountByCondition(new EstimateAmountNo(2), new EstimateAmount(499))
					));
		});
	}

	/**
	 * input: 目安金額リスト[N].金額  < 目安金額リスト[N+1].金額
	 * output: エラーではない
	 */
	@Test
	public void create_estimateAmounts_success() {
		val estimatePrise = EstimateAmountList.create(Arrays.asList(
					  new EstimateAmountByCondition(new EstimateAmountNo(1), new EstimateAmount(500))
					, new EstimateAmountByCondition(new EstimateAmountNo(2), new EstimateAmount(501))
				));

		assertThat(estimatePrise.getEstimatePrices())
						.extracting(d->d.getEstimateAmountNo().v(), d->d.getEstimateAmount().v())
						.containsExactly( tuple(1, 500)
										, tuple(2, 501));
	}


	/**
	 * Target	: getEstimateAmountByNo
	 */
	@Test
	public void test_getEstimateAmountByNo() {

		// 目安金額リスト
		val amountList = EstimateAmountList.create(Arrays.asList(
				EstimateAmountHelper.createAmountPerFrame( 1,  500 )
			,	EstimateAmountHelper.createAmountPerFrame( 2, 1000 )
			,	EstimateAmountHelper.createAmountPerFrame( 3, 1500 )
		));


		// 該当枠NOあり
		{
			// 枠NO
			val no = new EstimateAmountNo( 3 );

			// Execute
			val result = amountList.getEstimateAmountByNo( no );

			// Assertion
			assertThat( result ).isPresent();
			assertThat( result.get().getEstimateAmountNo() ).isEqualTo( no );
			assertThat( result.get().getEstimateAmount() ).isEqualTo( new EstimateAmount( 1500 ) );
		}


		// 該当枠NOなし
		{
			// Execute
			val result = amountList.getEstimateAmountByNo( new EstimateAmountNo( 4 ) );

			// Assertion
			assertThat( result ).isEmpty();
		}

	}


	/**
	 * Target	: getStepOfEstimateAmount
	 * @param require require
	 */
	@Test
	public void test_getStepOfEstimateAmount(@Injectable EstimateAmountList.Require req4test) {

		// 目安金額の扱い
		val handling = EstimateAmountHelper.createHandling(1);
		// 目安金額リスト
		val instance = EstimateAmountList.create(Arrays.asList(
						EstimateAmountHelper.createAmountPerFrame( 1, 1000 )
					,	EstimateAmountHelper.createAmountPerFrame( 2, 1200 )
					,	EstimateAmountHelper.createAmountPerFrame( 3, 1500 )
					,	EstimateAmountHelper.createAmountPerFrame( 4, 2000 )
				));
		// Mockup設定
		EstimateAmountHelper.mockupRequireForStepOfEstimateAmount(require, handling);
		EstimateAmountHelper.mockupRequireForStepOfEstimateAmount(req4test, handling);


		// 期待値
		@SuppressWarnings("serial")
		val expected = new HashMap<Integer, StepOfEstimateAmount>() {{
			// Key: 値, Value: 取得対象の『目安金額の段階』
			put(  999, EstimateAmountHelper.createStep(req4test, 1,    0, Optional.of(1000)) );
			put( 1200, EstimateAmountHelper.createStep(req4test, 3, 1200, Optional.of(1500)) );
			put( 1549, EstimateAmountHelper.createStep(req4test, 4, 1500, Optional.of(2000)) );
			put( 2974, EstimateAmountHelper.createStep(req4test, 4, 2000, Optional.empty()) );
		}};


		// Execute
		val result = expected.entrySet().stream()
				.collect(Collectors.toMap( Map.Entry::getKey
								, entry -> instance.getStepOfEstimateAmount( require, new EstimateAmount( entry.getKey() ) )
							));


		// Assertion
		assertThat( result ).containsExactlyInAnyOrderEntriesOf( expected );

	}

}
