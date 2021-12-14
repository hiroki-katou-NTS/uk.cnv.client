package nts.uk.ctx.exio.dom.input.util;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;

/**
 * 正常系と異常系の2種類の戻り値を表現するクラス（Rightが正常系）
 *
 * @param <L> 異常系の型
 * @param <R> 正常系の型
 */
public interface Either<L, R> {
	
	/**
	 * 正常系のインスタンスを作る
	 * @param right
	 * @return
	 */
	public static <L, R> Either<L, R> right(R right) {
		return new Right<>(right);
	}
	
	/**
	 * 異常系のインスタンスを作る
	 * @param left
	 * @return
	 */
	public static <L, R> Either<L, R> left(L left) {
		return new Left<>(left);
	}
	
	/**
	 * RightがVoidなEitherの正常系インスタンスを作る
	 * @return
	 */
	public static <L> Either<L, Void> rightVoid() {
		return new Right<>(null);
	}
	
	/**
	 * LeftがVoidなEitherの異常系インスタンスを作る
	 * @param left
	 * @return
	 */
	public static <L> Either<L, Void> leftVoid(L left) {
		return new Left<>(left);
	}
	
	/**
	 * rightOptionalの値があればその値でRightを、無ければleftSupplierの戻り値でLeftを作る
	 * @param rightOptional
	 * @param leftSupplier
	 * @return
	 */
	public static <L, R> Either<L, R> rightOptional(Optional<R> rightOptional, Supplier<L> leftSupplier) {
		if (rightOptional.isPresent()) {
			return Either.right(rightOptional.get());
		} else {
			return Either.left(leftSupplier.get());
		}
	}
	
	/**
	 * rightSupplierによって正常系のインスタンスを作るが、指定された例外がスローされた場合は異常系インスタンスを作る
	 * @param rightSupplier
	 * @param exceptionClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <L extends Exception, R> Either<L, R> tryCatch(Supplier<R> rightSupplier, Class<L> exceptionClass) {
		
		try {
			return right(rightSupplier.get());
		} catch (Exception ex) {
			if (ex.getClass().equals(exceptionClass)) {
				return Either.left((L) ex);
			}
			
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Rightならtrueを返す
	 * @return
	 */
	boolean isRight();
	
	/**
	 * Leftならtrueを返す
	 * @return
	 */
	default boolean isLeft() {
		return !isRight();
	}
	
	/**
	 * Rightであればtaskに値を渡して実行する
	 * @param task
	 * @return
	 */
	default Either<L, R> ifRight(Consumer<R> task) {
		return this;
	}
	
	/**
	 * Leftであればtaskに値を渡して実行する
	 * @param task
	 * @return
	 */
	default Either<L, R> ifLeft(Consumer<L> task) {
		return this;
	}
	
	/**
	 * Rightの値を返す。もしLeftならばRuntimeException
	 * @return
	 */
	default R getRight() {
		return getOrElseThrow(l -> new RuntimeException("this is Left: " + l));
	}
	
	/**
	 * Leftの値を返す。もしRightならRuntimeException
	 * @return
	 */
	L getLeft();
	
	/**
	 * Rightであればそれを、Leftであればotherの実行結果を返す
	 * @param other
	 * @return
	 */
	R getOrElseGet(Function<? super L,? extends R> other);
	
	/**
	 * Rightであればそれを返し、LeftであればexceptionFunctionが返した例外オブジェクトをスローする
	 * @param exceptionFunction
	 * @return
	 */
	<X extends RuntimeException> R getOrElseThrow(Function<? super L, X> exceptionFunction);

	/**
	 * LeftとRightを両方変換する
	 * @param leftMapper
	 * @param rightMapper
	 * @return
	 */
	<L2, R2> Either<L2, R2> map(
			Function<? super L, ? extends L2> leftMapper,
			Function<? super R, ? extends R2> rightMapper);
	
	/**
	 * Rightを変換する
	 * @param rightMapper
	 * @return
	 */
	default <R2> Either<L, R2> map(Function<? super R, ? extends R2> rightMapper) {
		return map(l -> l, rightMapper);
	}
	
	/**
	 * Rightを変換するが、その戻り値がLeftであればLeftに変換する
	 * @param rightEitherMapper
	 * @return
	 */
	<R2> Either<L, R2> mapEither(Function<? super R, Either<L, R2>> rightEitherMapper);
	
	/**
	 * Leftを変換する
	 * @param leftMapper
	 * @return
	 */
	default <L2> Either<L2, R> mapLeft(Function<? super L, ? extends L2> leftMapper) {
		return map(leftMapper, r -> r);
	}

	@RequiredArgsConstructor
	@ToString
	@EqualsAndHashCode
	public static class Right<L, R> implements Either<L, R> {

		private final R value;
		
		@Override
		public boolean isRight() {
			return true;
		}
		
		@Override
		public Either<L, R> ifRight(Consumer<R> task) {
			task.accept(value);
			return this;
		}
		
		@Override
		public L getLeft() {
			throw new RuntimeException("this is Right: " + value);
		}

		@Override
		public R getOrElseGet(Function<? super L, ? extends R> other) {
			return value;
		}

		@Override
		public <X extends RuntimeException> R getOrElseThrow(Function<? super L, X> exceptionFunction) {
			return value;
		}

		@Override
		public <L2, R2> Either<L2, R2> map(
				Function<? super L, ? extends L2> leftMapper,
				Function<? super R, ? extends R2> rightMapper) {
			return Either.right(rightMapper.apply(value));
		}

		@Override
		public <R2> Either<L, R2> mapEither(Function<? super R, Either<L, R2>> rightEitherMapper) {
			return rightEitherMapper.apply(value);
		}
		
	}

	@RequiredArgsConstructor
	@ToString
	@EqualsAndHashCode
	public static class Left<L, R> implements Either<L, R> {

		private final L value;
		
		@Override
		public boolean isRight() {
			return false;
		}
		
		@Override
		public Either<L, R> ifLeft(Consumer<L> task) {
			task.accept(value);
			return this;
		}
		@Override
		public L getLeft() {
			return value;
		}

		@Override
		public R getOrElseGet(Function<? super L, ? extends R> other) {
			return other.apply(value);
		}

		@Override
		public <X extends RuntimeException> R getOrElseThrow(Function<? super L, X> exceptionFunction) {
			throw exceptionFunction.apply(value);
		}

		@Override
		public <L2, R2> Either<L2, R2> map(
				Function<? super L, ? extends L2> leftMapper,
				Function<? super R, ? extends R2> rightMapper) {
			return Either.left(leftMapper.apply(value));
		}

		@Override
		public <R2> Either<L, R2> mapEither(Function<? super R, Either<L, R2>> rightEitherMapper) {
			return Either.left(value);
		}
		
	}
	
	/**
	 * 空のEitherシーケンスを作る
	 * @return
	 */
	public static <L, R> Sequence<L, R> sequenceEmpty() {
		return new Sequence<>(Collections.emptyList());
	}
	
	/**
	 * rightsをRight値として持つEitherのシーケンスを作る
	 * @param rights
	 * @return
	 */
	public static <R> Sequence<Void, R> sequenceOf(Collection<R> rights) {
		
		val list = rights.stream()
				.map(e -> Either.<Void, R>right(e))
				.collect(toList());
		
		return new Sequence<>(list);
	}
	
	/**
	 * leftsをLeft値、rightsをRight値としてマージしたEitherシーケンスを作る
	 * @param lefts
	 * @param rights
	 * @return
	 */
	public static <L, R> Sequence<L, R> sequenceOf(Collection<L> lefts, Collection<R> rights) {
		
		List<Either<L, R>> list = new ArrayList<>();
		lefts.forEach(l -> list.add(Either.left(l)));
		rights.forEach(r -> list.add(Either.right(r)));

		return new Sequence<>(list);
	}
	
	/**
	 * 複数のEitherを一括処理をするためのEitherシーケンス
	 *
	 * @param <L>
	 * @param <R>
	 */
	@RequiredArgsConstructor
	public static class Sequence<L, R> {
		
		private final List<Either<L, R>> list;
		
		/**
		 * 複数のシーケンスを1つのシーケンスとしてマージする
		 * @param sequences
		 * @return
		 */
		public static <L, R> Sequence<L, R> merge(List<Sequence<L, R>> sequences) {
			
			List<Either<L, R>> newList = new ArrayList<>();
			
			for (val s : sequences) {
				newList.addAll(s.list);
			}
			
			return new Sequence<>(newList);
		}
		
		/**
		 * シーケンス内のRightに対してmapEitherを適用した結果のシーケンスを返す（元々のLeftは破棄される）
		 * @param rightEitherMapper
		 * @return
		 */
		public <L2, R2> Sequence<L2, R2> mapEither(Function<? super R, Either<L2, R2>> rightEitherMapper) {
			
			List<Either<L2, R2>> newList = streamRight()
					.map(rightEitherMapper)
					.collect(toList());

			return new Sequence<>(newList);
		}
		
		/**
		 * シーケンス内の各Eitherに対してmapを適用した結果のシーケンスを返す（元々のLeftはそのまま）
		 * @param rightMapper
		 * @return
		 */
		public <R2> Sequence<L, R2> map(Function<? super R, R2> rightMapper) {

			List<Either<L, R2>> newList = new ArrayList<>();
			
			list.forEach(e -> {
				if (e.isRight()) {
					newList.add(Either.right(rightMapper.apply(e.getRight())));
				} else {
					newList.add(Either.left(e.getLeft()));
				}
			});

			return new Sequence<>(newList);
		}
		
		/**
		 * シーケンス内の各Eitherに対してmapLeftを適用した結果のシーケンスを返す（元々のRightはそのまま）
		 * @param leftMapper
		 * @return
		 */
		public <L2> Sequence<L2, R> mapLeft(Function<? super L, L2> leftMapper) {

			List<Either<L2, R>> newList = new ArrayList<>();
			
			list.forEach(e -> {
				if (e.isLeft()) {
					newList.add(Either.left(leftMapper.apply(e.getLeft())));
				} else {
					newList.add(Either.right(e.getRight()));
				}
			});

			return new Sequence<>(newList);
		}
		
		/**
		 * シーケンス内のRightに対して、predicateがtrueとなるものをRight、falseになるものをLeftとしたシーケンスを返す
		 * @param predicate
		 * @return
		 */
		public Sequence<R, R> separate(Predicate<R> predicate) {
			
			List<Either<R, R>> newList = new ArrayList<>();
			streamRight().forEach(r -> {
				if (predicate.test(r)) {
					newList.add(Either.right(r));
				} else {
					newList.add(Either.left(r));
				}
			});

			return new Sequence<>(newList);
		}
		
		/**
		 * シーケンス内の各Rightに対して処理taskを実行する
		 * @param task
		 * @return
		 */
		public Sequence<L, R> ifRight(Consumer<R> task) {
			streamRight().forEach(task);
			return this;
		}
		
		/**
		 * シーケンス内の各Leftに対して処理taskを実行する
		 * @param task
		 * @return
		 */
		public Sequence<L, R> ifLeft(Consumer<L> task) {
			streamLeft().forEach(task);
			return this;
		}
		
		/**
		 * シーケンス内のRightをListとして返す
		 * @return
		 */
		public List<R> listRight() {
			return streamRight().collect(toList());
		}
		
		/**
		 * シーケンス内のLeftをListとして返す
		 * @return
		 */
		public List<L> listLeft() {
			return streamLeft().collect(toList());
		}

		/**
		 * シーケンス内のRightをStreamとして返す
		 * @return
		 */
		public Stream<R> streamRight() {
			return list.stream()
					.filter(e -> e.isRight())
					.map(e -> e.getRight());
		}

		/**
		 * シーケンス内のLeftをStreamとして返す
		 * @return
		 */
		public Stream<L> streamLeft() {
			return list.stream()
					.filter(e -> e.isLeft())
					.map(e -> e.getLeft());
		}
	}
}
