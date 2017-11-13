package de.ameyering.wgplaner.wgplaner.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import java.util.ArrayList;

public class RecyclerViewItemAnimator extends SimpleItemAnimator {
    private ArrayList<ViewHolder> mPendingAdditions = new ArrayList<>();
    private ArrayList<ViewHolder> mPendingRemovals = new ArrayList<>();
    private ArrayList<MoveInfo> mPendingMoves = new ArrayList<>();
    private ArrayList<ChangeInfo> mPendingChanges = new ArrayList<>();

    private ArrayList<ArrayList<ViewHolder>> mAdditionsList = new ArrayList<>();
    private ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList<>();
    private ArrayList<ArrayList<ChangeInfo>> mChangesInfo = new ArrayList<>();

    private ArrayList<ViewHolder> mAddAnimations = new ArrayList<>();
    private ArrayList<ViewHolder> mRemoveAnimations = new ArrayList<>();
    private ArrayList<ViewHolder> mMoveAnimations = new ArrayList<>();
    private ArrayList<ViewHolder> mChangeAnimations = new ArrayList<>();

    private class MoveInfo {
        public ViewHolder holder;

        public int fromX;
        public int toX;
        public int fromY;
        public int toY;

        public MoveInfo(ViewHolder holder, int fromX, int toX, int fromY, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.toX = toX;
            this.fromY = fromY;
            this.toY = toY;
        }
    }
    private class ChangeInfo {
        public ViewHolder oldHolder;
        public ViewHolder newHolder;

        public int fromX;
        public int toX;
        public int fromY;
        public int toY;

        public ChangeInfo(ViewHolder oldHolder, ViewHolder newHolder, int fromX, int toX, int fromY,
            int toY) {
            this.oldHolder = oldHolder;
            this.newHolder = newHolder;
            this.fromX = fromX;
            this.toX = toX;
            this.fromY = fromY;
            this.toY = toY;
        }
    }

    @Override
    public boolean animateAdd(ViewHolder holder) {
        endAnimation(holder);
        holder.itemView.setAlpha(0);
        mPendingAdditions.add(holder);

        return true;
    }

    @Override
    public boolean animateRemove(ViewHolder holder) {
        endAnimation(holder);
        mPendingRemovals.add(holder);

        return true;
    }

    @Override
    public boolean animateChange(ViewHolder oldHolder, ViewHolder newHolder, int fromLeft, int fromTop,
        int toLeft, int toTop) {
        return false;
    }

    @Override
    public boolean animateMove(ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        return false;
    }

    @Override
    public void runPendingAnimations() {
        boolean pendingRemovals = !mPendingRemovals.isEmpty();
        boolean pendingAdditions = !mPendingAdditions.isEmpty();
        boolean pendingMoves = !mPendingMoves.isEmpty();
        boolean pendingChanges = !mPendingChanges.isEmpty();

        if (!pendingAdditions && !pendingChanges && !pendingMoves && !pendingRemovals) {
            return;
        }

        for (ViewHolder holder : mPendingRemovals) {
            runAnimateRemove(holder);
        }

        mPendingRemovals.clear();

        if (pendingMoves) {
            final ArrayList<MoveInfo> moves = new ArrayList<>();
            moves.addAll(mPendingMoves);
            mMovesList.add(moves);
            mPendingMoves.clear();

            Runnable mover = new Runnable() {
                @Override
                public void run() {
                    for (MoveInfo moveInfo : moves) {
                        runAnimateMove(moveInfo.holder, moveInfo.fromX, moveInfo.toX, moveInfo.fromY, moveInfo.toY);
                    }

                    moves.clear();
                    mMovesList.remove(moves);
                }
            };

            if (pendingRemovals) {
                View view = moves.get(0).holder.itemView;
                ViewCompat.postOnAnimationDelayed(view, mover, getRemoveDuration());

            } else {
                mover.run();
            }
        }

        if (pendingChanges) {

        }
    }

    @Override
    public void endAnimations() {

    }

    @Override
    public void endAnimation(ViewHolder item) {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    private void runAnimateAdd(final ViewHolder holder) {
        final View view = holder.itemView;
        mAddAnimations.add(holder);

        final AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "translationY", 100);
        animatorY.setDuration(200);

        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 1);
        animatorAlpha.setDuration(50);

        animatorSet.playTogether(animatorY, animatorAlpha);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                dispatchAddStarting(holder);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                dispatchAddFinished(holder);
                mAddAnimations.remove(holder);
                dispatchAnimationsWhenDone();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                view.setAlpha(1f);
                view.setTranslationY(100);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }

    private void runAnimateRemove(final ViewHolder holder) {
        final View view = holder.itemView;
        mAddAnimations.add(holder);

        final AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "translationY", 0);
        animatorY.setDuration(200);

        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 0);
        animatorAlpha.setDuration(50);

        animatorSet.playTogether(animatorY, animatorAlpha);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                dispatchRemoveStarting(holder);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                dispatchRemoveFinished(holder);
                mRemoveAnimations.remove(holder);
                dispatchAnimationsWhenDone();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                view.setAlpha(0);
                view.setTranslationY(0);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }

    private void runAnimateMove(ViewHolder holder, int fromX, int toX, int fromY, int toY) {

    }

    private void dispatchAnimationsWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }
}
