package data.utils;

import utils.*;

import java.awt.*;

public final class C {


    public static final class EditorPreferencesKeys {

        public static final String DEFAULT_LOAD_FILE_DIR = "default_load_file_dir";
        public static final String PREFS_FILE_NAME = "prefs.dat";
    }

    public static final class Data {
        public static final long SKETCH_ID = -1;
        public static final int LEVEL_SIZE_PIXELS = (int) (68 * SharedC.WORLD_UNIT);
        public static final float EPSILON = 1;

        public static final class JsonKeys {
            public static final String LEFT = "left";
            public static final String RIGHT = "right";
            public static final String TOP = "top";
            public static final String BOTTOM = "bottom";
            public static final String ELEMENTS = "elements";
            public static final String ACTORS = "actors";
            public static final String X = "x";
            public static final String Y = "y";
            public static final String ID = "id";
            public static final String DIRECTION = "direction";
            public static final String CURRENT_SECTOR_ID = "currentSectorId";
            public static final String CURRENT_FLOOR_ALTITUDE = "currentFloorAltitude";
            public static final String CURRENT_CEILING_ALTITUDE = "currentCeilingAltitude";
            public static final String TYPE = "type";
            public static final String VERTICES = "vertices";
            public static final String NUMBER_OF_LINES = "numberOfLines";
            public static final String LINES = "lines";
            public static final String SRC = "src";
            public static final String SOLID = "solid";
            public static final String FRONT_SECTOR_ID = "frontSectorId";
            public static final String BACK_SECTOR_ID = "backSectorId";
            public static final String DST = "dst";
            public static final String FRONT_TEXTURE = "frontTexture";
            public static final String BACK_TEXTURE = "backTexture";
            public static final String HORIZONTAL_OFFSET = "horizontalOffset";
            public static final String VERTICAL_OFFSET = "verticalOffset";
            public static final String OPACITY = "opacity";
            public static final String NAME = "name";
            public static final String MIDDLE = "middle";
            public static final String SECTORS = "sectors";
            public static final String SUB_SECTORS = "subSectors";
            public static final String POINTS = "points";
            public static final String CONTAINER_ID = "containerId";
            public static final String FLOOR_ALTITUDE = "floorAltitude";
            public static final String CEIL_ALTITUDE = "ceilAltitude";
            public static final String FLOOR_TEXTURE = "floorTexture";
            public static final String CEILING_TEXTURE = "ceilingTexture";
            public static final String PROPERTIES = "properties";
            public static final String LATEST_ACTOR_ID = "latest_actor_id";
            public static final String LATEST_VERTEX_ID = "latest_vertex_id";
            public static final String LATEST_LINE_ID = "latest_line_id";
            public static final String LATEST_SECTOR_ID = "latest_sector_id";
        }
    }

    public static final class Views {
        public static final String FONT = "Serif";
        public static final String ERROR_LOAD_MAP_FAIL = "Failed to load map!";

        public static final class MainWindow {
            public static final int WINDOW_WIDTH = 1600;
            public static final String TITLE = "Coal Editor";
            public static final String TITLE_NO_PROJECT = "Unsaved Project";

        }

        public static final class Dialogs {
            public static final int DIALOG_BORDER = 10;
            public static final int OK_BUTTON_WIDTH = 60;
            public static final int OK_BUTTON_HEIGHT = 40;

        }

        public static final class TopMenu {

            public static final class View {
                public static final class SetGridDialog {
                    public static final String SPINNER_GRID_SIZE = "Grid Size";
                    public static final String LABEL = "Set grid size: ";
                    public static final String BUTTON_RESET_LABEL = "Reset to world unit";
                }

                public static final String LABEL = "View";
                public static final String GRID_SIZE = "Set grid size";
            }

            public static final class Sectors {
                public static final String LABEL = "Sectors";
                public static final String DELETE_SECTOR = "Delete selected sector(s)";
            }

            public static final class Windows {
                public static final String LABEL = "Windows";
                public static final String INFORMATION = "Information";
            }

        }

        public static final class StatusBar {
            public static final int HEIGHT = 32;
        }

        public static final class LevelViewSection {
            public static final int DEFAULT_GRID_SIZE = (int) SharedC.WORLD_UNIT;

            public static final int HEIGHT = 600;

            public static final int CURSOR_RADIUS = 10;
            public static final int CURSOR_QUAD_MAP_RADIUS = 64;
            public static final float MAGNET_DIST = 20;
            public static final Color OUTLINE_COLOR = Color.YELLOW;
            public static final int SCROLL_SPEED = 32;

            public static final class Border {
                public static final Color BORDER_COLOR = new Color(32, 32, 32);
                public static final int BORDER_SIZE = 20;
            }

            public enum ElementColor {
                PLAYER(new Color(0, 150, 0), Color.GREEN, new Color(100, 255, 0), new Color(200, 255, 200)),
                PICKUP(new Color(0, 0, 200), new Color(0, 100, 255), new Color(0, 200, 255), new Color(0, 255, 255)),
                VERTEX(new Color(0, 100, 200), new Color(0, 255, 255), new Color(0, 200, 255), new Color(0, 255, 255)),
                LINE(LineDisplay.Colors.SOLID, Color.YELLOW, Color.GREEN, Color.CYAN),
                ENEMY(new Color(150, 0, 0), Color.RED, new Color(255, 100, 0), new Color(255, 200, 200)),
                SECTOR(null, new Color(255, 0, 255), new Color(255, 100, 255), new Color(255, 200, 255));

                private final Color regular;
                private final Color highlight;
                private final Color selected;
                private final Color both;

                ElementColor(Color regular, Color highlight, Color selected, Color both) {
                    this.regular = regular;
                    this.highlight = highlight;
                    this.selected = selected;
                    this.both = both;
                }

                public Color getRegular() {
                    return regular;
                }

                public Color getHighlight() {
                    return highlight;
                }

                public Color getSelected() {
                    return selected;
                }

                public Color getBoth() {
                    return both;
                }
            }

            public static final class VertexDisplay {
                public static final int VERTEX_DIAMETER = 8;
            }

            public static final class ActorDisplay {
                public static final String ACTOR_IMAGE = "resources/actor.png";
                public static final String ACTOR_DIR_IMAGE = "resources/actor_direction.png";
                public static final String ACTOR_NO_DIR_IMAGE = "resources/actor_no_direction.png";
                public static final double RADIUS = 32;

            }

            public static final class LineDisplay {
                public static final class Colors {
                    public static final Color SOLID = Color.WHITE;
                    public static final Color PASSABLE = Color.GRAY;
                }

                public static final double NORMAL_LENGTH = 6;
            }

            public static final class LevelElementDialog {
                public static final String ERROR_NEGATIVE_VALUE = "%s cannot have a negative value!";
                public static final String TEXTURE_TAB = "Textures";
                public static final String OPACITY_SPINNER = "Opacity";
                public static final int SECTION_PADDING = 10;

                public static final class Vertex {
                    public static final String TITLE = "Vertex %d";
                    public static final String TITLE_MULTIPLE = "Multiple Vertices";
                    public static final String LABEL_X = "X coordinate:";
                    public static final String LABEL_Y = "Y coordinate:";
                    public static final String MSG_MULTIPLE = "Multiple vertices are selected.";
                }

                public static final class Actor {
                    public static final String TYPE = "Type";
                    public static final String DIRECTION = "Direction:";
                    public static final int MAX_DEGREES = 299;
                    public static final String ACTORS = "Actors";
                    public static final String TITLE = "Actor %d";
                    public static final String TITLE_MULTIPLE = "Multiple Actors";
                    public static final String NAME_DIRECTION_SPINNER = "Direction";
                }

                public static final class Sector {
                    public static final String TITLE = "Sector %d";
                    public static final String TITLE_MULTIPLE = "Multiple Sectors";

                    public static final String FLOOR_ALTITUDE = "Floor Altitude:";
                    public static final String CEILING_ALTITUDE = "Ceiling Altitude:";
                    public static final String ALERT_ALTITUDES = "Ceiling altitude has to be higher than floor altitude!";
                    public static final String NAME_CEILING_SPINNER = "Ceiling Altitude";
                    public static final String NAME_FLOOR_SPINNER = "Floor Altitude";
                    public static final String NAME_TEX_CEILING = "Ceiling Texture";
                    public static final String NAME_TEX_FLOOR = "Floor Texture";
                }

                public static final class Line {
                    public static final String TITLE = "Line %d";
                    public static final String TITLE_MULTIPLE = "Multiple Lines";
                    public static final String SOLID = "Solid:";
                    public static final String SOURCE_VERTEX_ID = "Source Vertex: %s";
                    public static final String DESTINATION_VERTEX_ID = "Destination Vertex: %s";
                    public static final String FRONT_SECTOR_ID = "Front Sector: %s";
                    public static final String NO_ELEMENT = "-";
                    public static final String BACK_SECTOR_ID = "Back Sector: %s";
                    public static final String NAME_FRONT_MIDDLE_TEXTURE = "Front Middle Texture";
                    public static final String NAME_FRONT_BOTTOM_TEXTURE = "Front Bottom Texture";
                    public static final String NAME_FRONT_TOP_TEXTURE = "Front Top Texture";
                    public static final String NAME_BACK_MIDDLE_TEXTURE = "Back Middle Texture";
                    public static final String NAME_BACK_BOTTOM_TEXTURE = "Back Bottom Texture";
                    public static final String NAME_BACK_TOP_TEXTURE = "Back Top Texture";
                    public static final String SPINNER_NAME_HOR_TEX = "%s Horizontal Offset";
                    public static final String SPINNER_NAME_VER_TEX = "%s Vertical Offset";
                    public static final String FRONT_TEXTURES = "Front Textures:";
                    public static final String BACK_TEXTURES = "Back Textures:";
                }

                public static final String SETTINGS = "Settings";
                public static final String PROPERTIES_TAB_NAME = "Properties";
                public static final String OK = "OK";
            }


            public static final Color DRAWING_COLOR = Color.YELLOW;
        }

        public static final class InfoSection {
            public static final class ModeLabel {
                public static final int SIZE = 60;
                public static final int PADDING_TOP_LEFT = 10;
            }

            public static final String NAME = "info_section";
            public static final int HEIGHT = 300;
        }

        public static final class ToolBar {
            public static final String TOGGLE_SNAP_NAME = "snap";

            public static final class CommandsNames {

                public static final String SAVE = "save";
                public static final String LOAD = "load";
                public static final String UNDO = "undo";
                public static final String REDO = "redo";
            }

            public static final class Icons {
                public static final String SELECT_MODE_VERTICES = "resources/mode_vertices.png";
                public static final String SELECT_MODE_LINES = "resources/mode_lines.png";
                public static final String SELECT_MODE_SECTORS = "resources/mode_sectors.png";
                public static final String SELECT_MODE_ACTORS = "resources/mode_actors.png";
                public static final String SELECT_MODE_DRAWING = "resources/mode_drawing.png";
                public static final String TOGGLE_SNAP = "resources/snap.png";
                public static final String SAVE = "resources/save.png";
                public static final String LOAD = "resources/load.png";
                public static final String UNDO = "resources/undo.png";
                public static final String REDO = "resources/redo.png";
            }

            public static final class ToolTips {
                public static final String SELECT_MODE_VERTICES = "Select vertices mode";
                public static final String SELECT_MODE_LINES = "Select lines mode";
                public static final String SELECT_MODE_SECTORS = "Select sectors mode";
                public static final String SELECT_MODE_ACTORS = "Select actors mode";
                public static final String SELECT_MODE_DRAWING = "Select drawing mode";
                public static final String TOGGLE_SNAP = "Toggle snap to grid";
                public static final String SAVE = "Save map";
                public static final String LOAD = "Load map";
                public static final String UNDO = "Undo last action";
                public static final String REDO = "Redo last undone action";
            }

            public static final String NAME = "toolbar";
        }
    }

}
