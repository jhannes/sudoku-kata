package com.brodwall.kata.sudoku;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class SudokuSolverTest {
    private SudokuBoard board = mock(SudokuBoard.class);
    private SudokuSolver solver = new SudokuSolver(board);

    @Before
    public void allCellsAreFilled() {
        when(board.isFilled(anyInt(), anyInt())).thenReturn(true);
    }

    @Test
    public void shouldFindSolutionToFilledBoard() {
        assertThat(solver.findSolution(board)).isTrue();
    }

    @Test
    public void shouldNotFindSolutionWhenCellHasNoOptions() throws Exception {
        when(board.isFilled(8, 8)).thenReturn(false);
        when(board.getOptionsForCell(8,8)).thenReturn(noOptions());
        assertThat(solver.findSolution(board)).isFalse();
    }

    @Test
    public void shouldFindSolutionWhenCellHasOneOption() throws Exception {
        when(board.isFilled(8, 8)).thenReturn(false);
        when(board.getOptionsForCell(8,8)).thenReturn(oneOption(3));
        assertThat(solver.findSolution(board)).isTrue();
        verify(board).setCellValue(8,8, 3);
    }

    @Test
    public void shouldBacktrackWhenNoOptionsInFutureCell() throws Exception {
        when(board.isFilled(7, 8)).thenReturn(false);
        when(board.getOptionsForCell(7,8)).thenReturn(options(1,2,3));
        when(board.isFilled(8, 8)).thenReturn(false);
        when(board.getOptionsForCell(8,8)).thenReturn(noOptions()).thenReturn(oneOption(1));

        assertThat(solver.findSolution(board)).isTrue();

        verify(board).setCellValue(7,8, 2);
        verify(board).setCellValue(8,8, 1);
        verify(board, never()).setCellValue(7,8, 3);
    }

    @Test
    public void shouldClearCellWhenBacktracking() throws Exception {
        when(board.isFilled(7, 8)).thenReturn(false);
        when(board.getOptionsForCell(7,8)).thenReturn(options(1));
        when(board.isFilled(8, 8)).thenReturn(false);
        when(board.getOptionsForCell(8,8)).thenReturn(noOptions());

        assertThat(solver.findSolution(board)).isFalse();

        InOrder order = inOrder(board);
        order.verify(board).setCellValue(7,8, 1);
        order.verify(board).clearCell(7,8);
    }

    @Test
    public void shouldSolveCompletePuzzle() throws Exception {
        String puzzle = "..3.2.6..9..3.5..1..18.64....81.29..7.......8..67.82....26.95..8..2.3..9..5.1.3..";
        SudokuSolver solver = new SudokuSolver(puzzle);
        solver.solve();

        SudokuBoard board = solver.getBoard();
        String[] lines = board.dumpBoard().split("\n");
        assertThat(lines).hasSize(9);
        for (String line : lines) {
            assertThat(line).matches("[1-9]{9}");
        }
    }

    private List<Integer> options(Integer... options) {
        return Arrays.asList(options);
    }

    private List<Integer> oneOption(int option) {
        return Arrays.asList(option);
    }

    private List<Integer> noOptions() {
        return new ArrayList<Integer>();
    }
}
