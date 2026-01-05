defmodule SpaceGame.Application do
  use Application

  @impl true
  def start(_type, _args) do
    children = [
      {SpaceGame.GameState, []},
      {SpaceGame.TCPServer, [port: 4040]}
    ]

    opts = [strategy: :one_for_one, name: SpaceGame.Supervisor]
    Supervisor.start_link(children, opts)
  end
end
