defmodule SpaceGame.ClientHandler do
  use GenServer
  require Logger

  def start_link(socket) do
    GenServer.start_link(__MODULE__, socket)
  end

  @impl true
  def init(socket) do
    case SpaceGame.GameState.add_player(self()) do
      {:ok, player_id} ->
        :inet.setopts(socket, active: true)
<<<<<<< Updated upstream
        # Use camelCase key so Java client (Gson) maps it to `playerId`
=======
        # Use camelCase key so Java client maps it to `playerId`
>>>>>>> Stashed changes
        send(self(), {:send, %{type: "connection", playerId: player_id}})
        Logger.info("Client connected as Player #{player_id}")
        {:ok, %{socket: socket, player_id: player_id}}

      {:error, :game_full} ->
        response = Jason.encode!(%{type: "error", message: "Game is full"})
        :gen_tcp.send(socket, response)
        :gen_tcp.close(socket)
        {:stop, :normal}
    end
  end

  @impl true
  def handle_info({:tcp, _socket, data}, state) do
    case Jason.decode(data) do
      {:ok, message} ->
        handle_message(message, state)
      {:error, _} ->
        Logger.error("Failed to decode message: #{data}")
    end
    {:noreply, state}
  end

  @impl true
  def handle_info({:tcp_closed, _socket}, state) do
    Logger.info("Client disconnected: Player #{state.player_id}")
    SpaceGame.GameState.remove_player(self())
    {:stop, :normal, state}
  end

  @impl true
  def handle_info({:tcp_error, _socket, reason}, state) do
    Logger.error("TCP error: #{inspect(reason)}")
    {:stop, :normal, state}
  end

  @impl true
  def handle_info({:send, message}, state) do
    data = Jason.encode!(message)
    Logger.debug("Sending to player #{state.player_id}: #{data}")
    :gen_tcp.send(state.socket, data)
    {:noreply, state}
  end

  @impl true
  def handle_info({:game_start, player_id}, state) do
    send(self(), {:send, %{type: "game_start", your_player_id: player_id}})
    {:noreply, state}
  end

  @impl true
  def handle_info({:player_disconnected, player_id}, state) do
    send(self(), {:send, %{type: "player_disconnected", player_id: player_id}})
    {:noreply, state}
  end

  @impl true
  def handle_info({:state_update, game_state}, state) do
    # Convert player structs to simple maps without PIDs to avoid Jason encoding errors
    player_to_dto = fn
      nil -> nil
      p -> %{id: p.id, x: p.x, y: p.y, health: p.health}
    end

    message = %{
      type: "state_update",
      state: %{
        player1: player_to_dto.(game_state.player1),
        player2: player_to_dto.(game_state.player2),
        enemies: Enum.map(game_state.enemies || [], fn e -> e end)
      }
    }

    send(self(), {:send, message})
    {:noreply, state}
  end

  defp handle_message(%{"type" => "player_update"} = msg, state) do
    data = %{
      x: msg["x"],
      y: msg["y"],
      health: msg["health"]
    }
    SpaceGame.GameState.update_player(state.player_id, data)
    SpaceGame.GameState.broadcast_state()
  end

  defp handle_message(%{"type" => "enemy_update"} = msg, state) do
    SpaceGame.GameState.update_enemies(msg["enemies"])
    SpaceGame.GameState.broadcast_state()
  end

  defp handle_message(%{"type" => "ping"}, state) do
    send(self(), {:send, %{type: "pong"}})
  end

  defp handle_message(msg, _state) do
    Logger.warn("Unknown message type: #{inspect(msg)}")
  end
end
