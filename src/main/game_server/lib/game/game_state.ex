defmodule SpaceGame.GameState do
  use GenServer
  require Logger

  defstruct [:player1, :player2, :enemies, :game_started]

  # Client API
  def start_link(_) do
    GenServer.start_link(__MODULE__, %{}, name: __MODULE__)
  end

  def add_player(player_pid) do
    GenServer.call(__MODULE__, {:add_player, player_pid})
  end

  def remove_player(player_pid) do
    GenServer.cast(__MODULE__, {:remove_player, player_pid})
  end

  def update_player(player_id, data) do
    GenServer.cast(__MODULE__, {:update_player, player_id, data})
  end

  def update_enemies(enemies) do
    GenServer.cast(__MODULE__, {:update_enemies, enemies})
  end

  def get_state do
    GenServer.call(__MODULE__, :get_state)
  end

  def broadcast_state do
    GenServer.cast(__MODULE__, :broadcast_state)
  end

  # Server Callbacks
  @impl true
  def init(_) do
    state = %__MODULE__{
      player1: nil,
      player2: nil,
      enemies: [],
      game_started: false
    }
    Logger.info("GameState initialized")
    # Start periodic broadcast - every 50ms (20 times per second)
    :timer.send_interval(50, :broadcast_state)
    {:ok, state}
  end

  @impl true
  def handle_call({:add_player, player_pid}, _from, state) do
    cond do
      state.player1 == nil ->
        Logger.info("Player 1 connected: #{inspect(player_pid)}")
        new_state = %{state | player1: %{pid: player_pid, id: 1, x: 0, y: 0, health: 100}}
        {:reply, {:ok, 1}, new_state}

      state.player2 == nil ->
        Logger.info("Player 2 connected: #{inspect(player_pid)}")
        new_state = %{state | player2: %{pid: player_pid, id: 2, x: 100, y: 0, health: 100}, game_started: true}
        send_to_player(state.player1.pid, {:game_start, 1})
        send_to_player(player_pid, {:game_start, 2})
        {:reply, {:ok, 2}, new_state}

      true ->
        Logger.warn("Game full, rejecting player: #{inspect(player_pid)}")
        {:reply, {:error, :game_full}, state}
    end
  end

  @impl true
  def handle_call(:get_state, _from, state) do
    {:reply, state, state}
  end

  @impl true
  def handle_cast({:remove_player, player_pid}, state) do
    new_state = cond do
      state.player1 && state.player1.pid == player_pid ->
        Logger.info("Player 1 disconnected")
        if state.player2, do: send_to_player(state.player2.pid, {:player_disconnected, 1})
        %{state | player1: nil, game_started: false}

      state.player2 && state.player2.pid == player_pid ->
        Logger.info("Player 2 disconnected")
        if state.player1, do: send_to_player(state.player1.pid, {:player_disconnected, 2})
        %{state | player2: nil, game_started: false}

      true -> state
    end
    {:noreply, new_state}
  end

  @impl true
  def handle_cast({:update_player, player_id, data}, state) do
    new_state = case player_id do
      1 when state.player1 != nil ->
        %{state | player1: Map.merge(state.player1, data)}
      2 when state.player2 != nil ->
        %{state | player2: Map.merge(state.player2, data)}
      _ -> state
    end
    {:noreply, new_state}
  end

  @impl true
  def handle_cast({:update_enemies, enemies}, state) do
    {:noreply, %{state | enemies: enemies}}
  end

  @impl true
  def handle_cast(:broadcast_state, state) do
    if state.game_started do
      game_state = %{
        player1: state.player1,
        player2: state.player2,
        enemies: state.enemies
      }

      if state.player1, do: send_to_player(state.player1.pid, {:state_update, game_state})
      if state.player2, do: send_to_player(state.player2.pid, {:state_update, game_state})
    end
    {:noreply, state}
  end

  @impl true
  def handle_info(:broadcast_state, state) do
    if state.game_started do
      game_state = %{
        player1: state.player1,
        player2: state.player2,
        enemies: state.enemies
      }

      if state.player1, do: send_to_player(state.player1.pid, {:state_update, game_state})
      if state.player2, do: send_to_player(state.player2.pid, {:state_update, game_state})
    end
    {:noreply, state}
  end

  defp send_to_player(pid, message) do
    send(pid, message)
  end
end
