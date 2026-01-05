defmodule SpaceGame.TCPServer do
  use GenServer
  require Logger

  def start_link(opts) do
    GenServer.start_link(__MODULE__, opts, name: __MODULE__)
  end

  @impl true
  def init(opts) do
    port = Keyword.get(opts, :port, 4040)
    {:ok, listen_socket} = :gen_tcp.listen(port, [
      :binary,
      packet: 4,
      active: false,
      reuseaddr: true
    ])

    Logger.info("TCP Server listening on port #{port}")
    send(self(), :accept)
    {:ok, %{listen_socket: listen_socket}}
  end

  @impl true
  def handle_info(:accept, state) do
    {:ok, client_socket} = :gen_tcp.accept(state.listen_socket)
    {:ok, pid} = SpaceGame.ClientHandler.start_link(client_socket)
    :gen_tcp.controlling_process(client_socket, pid)

    send(self(), :accept)
    {:noreply, state}
  end
end
