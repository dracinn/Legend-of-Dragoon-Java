package legend.game.submap;

import legend.core.Config;
import legend.core.IoHelper;
import legend.core.MathHelper;
import legend.core.RenderEngine;
import legend.core.gpu.Bpp;
import legend.core.gpu.GpuCommandCopyVramToVram;
import legend.core.gpu.GpuCommandLine;
import legend.core.gpu.GpuCommandPoly;
import legend.core.gte.GsCOORDINATE2;
import legend.core.gte.MV;
import legend.core.gte.ModelPart10;
import legend.core.gte.TmdObjTable1c;
import legend.core.memory.Method;
import legend.core.opengl.PolyBuilder;
import legend.core.opengl.TmdObjLoader;
import legend.game.EngineState;
import legend.game.EngineStateEnum;
import legend.game.fmv.Fmv;
import legend.game.input.Input;
import legend.game.input.InputAction;
import legend.game.inventory.WhichMenu;
import legend.game.modding.coremod.CoreMod;
import legend.game.scripting.FlowControl;
import legend.game.scripting.Param;
import legend.game.scripting.RunningScript;
import legend.game.scripting.ScriptDescription;
import legend.game.scripting.ScriptParam;
import legend.game.scripting.ScriptState;
import legend.game.scripting.ScriptStorageParam;
import legend.game.tim.Tim;
import legend.game.tmd.Renderer;
import legend.game.types.ActiveStatsa0;
import legend.game.types.AnimatedSprite08;
import legend.game.types.AnmFile;
import legend.game.types.AnmSpriteGroup;
import legend.game.types.AnmSpriteMetrics14;
import legend.game.types.CContainer;
import legend.game.types.CharacterData2c;
import legend.game.types.GsF_LIGHT;
import legend.game.types.LodString;
import legend.game.types.Model124;
import legend.game.types.NewRootStruct;
import legend.game.types.ShopStruct40;
import legend.game.types.SmallerStruct;
import legend.game.types.Textbox4c;
import legend.game.types.TextboxChar08;
import legend.game.types.TextboxText84;
import legend.game.types.TextboxType;
import legend.game.types.TmdAnimationFile;
import legend.game.types.Translucency;
import legend.game.unpacker.FileData;
import legend.game.unpacker.Unpacker;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static legend.core.GameEngine.CONFIG;
import static legend.core.GameEngine.GPU;
import static legend.core.GameEngine.GTE;
import static legend.core.GameEngine.RENDERER;
import static legend.core.GameEngine.SCRIPTS;
import static legend.core.MathHelper.cos;
import static legend.core.MathHelper.flEq;
import static legend.core.MathHelper.psxDegToRad;
import static legend.core.MathHelper.sin;
import static legend.game.SItem.loadCharacterStats;
import static legend.game.Scus94491BpeSegment.getLoadedDrgnFiles;
import static legend.game.Scus94491BpeSegment.loadDir;
import static legend.game.Scus94491BpeSegment.loadDrgnDir;
import static legend.game.Scus94491BpeSegment.loadFile;
import static legend.game.Scus94491BpeSegment.orderingTableBits_1f8003c0;
import static legend.game.Scus94491BpeSegment.resizeDisplay;
import static legend.game.Scus94491BpeSegment.startFadeEffect;
import static legend.game.Scus94491BpeSegment.tmdGp0Tpage_1f8003ec;
import static legend.game.Scus94491BpeSegment.zOffset_1f8003e8;
import static legend.game.Scus94491BpeSegment_8002.FUN_800218f0;
import static legend.game.Scus94491BpeSegment_8002.FUN_8002246c;
import static legend.game.Scus94491BpeSegment_8002.FUN_8002a9c0;
import static legend.game.Scus94491BpeSegment_8002.animateModel;
import static legend.game.Scus94491BpeSegment_8002.applyModelRotationAndScale;
import static legend.game.Scus94491BpeSegment_8002.calculateAppropriateTextboxBounds;
import static legend.game.Scus94491BpeSegment_8002.clearTextbox;
import static legend.game.Scus94491BpeSegment_8002.clearTextboxText;
import static legend.game.Scus94491BpeSegment_8002.initModel;
import static legend.game.Scus94491BpeSegment_8002.initObjTable2;
import static legend.game.Scus94491BpeSegment_8002.loadAndRenderMenus;
import static legend.game.Scus94491BpeSegment_8002.loadModelStandardAnimation;
import static legend.game.Scus94491BpeSegment_8002.prepareObjTable2;
import static legend.game.Scus94491BpeSegment_8002.scriptDeallocateAllTextboxes;
import static legend.game.Scus94491BpeSegment_8002.srand;
import static legend.game.Scus94491BpeSegment_8002.textboxFits;
import static legend.game.Scus94491BpeSegment_8003.GetTPage;
import static legend.game.Scus94491BpeSegment_8003.GsGetLs;
import static legend.game.Scus94491BpeSegment_8003.GsGetLw;
import static legend.game.Scus94491BpeSegment_8003.GsGetLws;
import static legend.game.Scus94491BpeSegment_8003.GsInitCoordinate2;
import static legend.game.Scus94491BpeSegment_8003.GsSetFlatLight;
import static legend.game.Scus94491BpeSegment_8003.GsSetLightMatrix;
import static legend.game.Scus94491BpeSegment_8003.PopMatrix;
import static legend.game.Scus94491BpeSegment_8003.PushMatrix;
import static legend.game.Scus94491BpeSegment_8003.RotTransPers4;
import static legend.game.Scus94491BpeSegment_8003.perspectiveTransform;
import static legend.game.Scus94491BpeSegment_8004.engineStateOnceLoaded_8004dd24;
import static legend.game.Scus94491BpeSegment_8004.engineState_8004dd20;
import static legend.game.Scus94491BpeSegment_8005.collidedPrimitiveIndex_80052c38;
import static legend.game.Scus94491BpeSegment_8005.renderBorder_80052b68;
import static legend.game.Scus94491BpeSegment_8005.shouldRestoreCameraPosition_80052c40;
import static legend.game.Scus94491BpeSegment_8005.submapCutForSave_800cb450;
import static legend.game.Scus94491BpeSegment_8005.submapCut_80052c30;
import static legend.game.Scus94491BpeSegment_8005.submapEnvState_80052c44;
import static legend.game.Scus94491BpeSegment_8005.submapScene_80052c34;
import static legend.game.Scus94491BpeSegment_8005.textboxMode_80052b88;
import static legend.game.Scus94491BpeSegment_8005.textboxTextType_80052ba8;
import static legend.game.Scus94491BpeSegment_8007.vsyncMode_8007a3b8;
import static legend.game.Scus94491BpeSegment_800b._800bd7b0;
import static legend.game.Scus94491BpeSegment_800b.battleStage_800bb0f4;
import static legend.game.Scus94491BpeSegment_800b.drgnBinIndex_800bc058;
import static legend.game.Scus94491BpeSegment_800b.encounterId_800bb0f8;
import static legend.game.Scus94491BpeSegment_800b.fullScreenEffect_800bb140;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Scus94491BpeSegment_800b.loadedDrgnFiles_800bcf78;
import static legend.game.Scus94491BpeSegment_800b.loadingNewGameState_800bdc34;
import static legend.game.Scus94491BpeSegment_800b.musicLoaded_800bd782;
import static legend.game.Scus94491BpeSegment_800b.playerPositionBeforeBattle_800bed30;
import static legend.game.Scus94491BpeSegment_800b.pregameLoadingStage_800bb10c;
import static legend.game.Scus94491BpeSegment_800b.rview2_800bd7e8;
import static legend.game.Scus94491BpeSegment_800b.screenOffsetBeforeBattle_800bed50;
import static legend.game.Scus94491BpeSegment_800b.scriptStatePtrArr_800bc1c0;
import static legend.game.Scus94491BpeSegment_800b.shadowModel_800bda10;
import static legend.game.Scus94491BpeSegment_800b.sobjPositions_800bd818;
import static legend.game.Scus94491BpeSegment_800b.stats_800be5f8;
import static legend.game.Scus94491BpeSegment_800b.submapFullyLoaded_800bd7b4;
import static legend.game.Scus94491BpeSegment_800b.submapId_800bd808;
import static legend.game.Scus94491BpeSegment_800b.textboxText_800bdf38;
import static legend.game.Scus94491BpeSegment_800b.textboxes_800be358;
import static legend.game.Scus94491BpeSegment_800b.transitioningFromCombatToSubmap_800bd7b8;
import static legend.game.Scus94491BpeSegment_800b.whichMenu_800bdc38;
import static legend.game.Scus94491BpeSegment_800c.lightColourMatrix_800c3508;
import static legend.game.Scus94491BpeSegment_800c.lightDirectionMatrix_800c34e8;
import static legend.game.Scus94491BpeSegment_800c.worldToScreenMatrix_800c3548;
import static org.lwjgl.opengl.GL11C.GL_LINES;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLE_STRIP;

public class SMap extends EngineState {
  private int fmvIndex_800bf0dc;

  private EngineStateEnum afterFmvLoadingStage_800bf0ec = EngineStateEnum.PRELOAD_00;

  private final GsF_LIGHT GsF_LIGHT_0_800c66d8 = new GsF_LIGHT();
  private final GsF_LIGHT GsF_LIGHT_1_800c66e8 = new GsF_LIGHT();
  private final GsF_LIGHT GsF_LIGHT_2_800c66f8 = new GsF_LIGHT();
  private int chapterTitleState_800c6708;
  private int chapterTitleAnimationTicksRemaining_800c670a;
  private int chapterTitleDropShadowOffsetX_800c670c;
  private int chapterTitleDropShadowOffsetY_800c670e;
  private List<FileData> chapterTitleCardMrg_800c6710;
  private int chapterTitleNumberOffsetX_800c6714;
  private int chapterTitleNumberOffsetY_800c6718;
  private int chapterTitleNameOffsetX_800c671c;
  private int chapterTitleNameOffsetY_800c6720;
  /** Inverted condition from retail */
  private boolean chapterTitleIsTranslucent_800c6724;
  private int chapterTitleBrightness_800c6728;

  public int sobjCount_800c6730;

  /**
   * Lower 4 bits are chapter title num (starting at 1), used for displaying chapter title cards
   *
   * 0x80 bit indicates that the origin XY of the title card have been set and the animation is ready to start rendering
   */
  private int chapterTitleNum_800c6738;

  private int chapterTitleAnimationPauseTicksRemaining_800c673c;
  public ScriptState<Void> submapControllerState_800c6740;

  private final Model124 playerModel_800c6748 = new Model124("Player");

  private boolean chapterTitleAnimationComplete_800c686e;
  private boolean unloadSubmapParticles_800c6870;

  private int chapterTitleOriginX_800c687c;
  private int chapterTitleOriginY_800c687e;
  public final ScriptState<SubmapObject210>[] sobjs_800c6880 = new ScriptState[20];

  public Submap submap;

  private boolean chapterTitleCardLoaded_800c68e0;

  private SubmapMediaState mediaLoadingStage_800c68e4;
  private final SubmapCaches80 caches_800c68e8 = new SubmapCaches80();

  /** Index 31 tracks the current tick since indicator last enabled. Have not yet seen other elements set to anything but -1 */
  public final int[] indicatorTickCountArray_800c6970 = new int[32];

  private TriangleIndicator140 triangleIndicator_800c69fc;

  private final Vector3f cameraPos_800c6aa0 = new Vector3f();

  private final Vector3f prevPlayerPos_800c6ab0 = new Vector3f();
  private float encounterMultiplier_800c6abc;
  private final MV playerPositionWhenLoadingSubmap_800c6ac0 = new MV();
  private int smapTicks_800c6ae0;
  /** Note: a negative value for some reason, counts up to 0 */
  private int ticksUntilEncountersAreEnabled_800c6ae4;
  public int encounterAccumulator_800c6ae8;
  private final List<CountdownLatch> latchList_800c6aec = new ArrayList<>();

  private int currentSubmapScene_800caaf8;

  private NewRootStruct newrootPtr_800cab04;

  private final MapTransitionData4c mapTransitionData_800cab24 = new MapTransitionData4c();
  private int mapTransitionTicks_800cab28;

  public SubmapState smapLoadingStage_800cb430 = SubmapState.INIT_0;

  private boolean returnedToSameSubmapAfterBattle_800cb448;

  private final Vector2i screenOffset_800cb568 = new Vector2i();

  private CountdownLatch screenOffsetLatch_800cbd38;
  private CountdownLatch geomOffsetLatch_800cbd3c;
  private final MV screenToWorldMatrix_800cbd40 = new MV();

  private final CollisionGeometry collisionGeometry_800cbe08 = new CollisionGeometry(this);

  private SnowEffect snow_800d4bd8;

  private final Model124 tmdDustModel_800d4d40 = new Model124("Dust");

  private final OrthoTrailParticle54 orthoQuadTrail = new OrthoTrailParticle54();

  private final TmdTrailParticle20 tmdTrail_800d4ec0 = new TmdTrailParticle20();

  private final SmokeParticleEffect smokeCloudEffect_800d4f50 = new SmokeParticleEffect();

  private final LawPodTrailSegment34 lawPodTrail_800d4f90 = new LawPodTrailSegment34();

  private final TrailSegmentVertices14 lawPodTrailVerts_800d4fd0 = new TrailSegmentVertices14();

  private final TriangleIndicator44[] _800d4ff0 = new TriangleIndicator44[21];
  {
    Arrays.setAll(this._800d4ff0, i -> new TriangleIndicator44());
  }

  private final AnimatedSprite08 playerIndicatorAnimation_800d5588 = new AnimatedSprite08();
  private final AnimatedSprite08 doorIndicatorAnimation_800d5590 = new AnimatedSprite08();
  private final SavePointRenderData44[] savePoint_800d5598 = new SavePointRenderData44[2];
  {
    Arrays.setAll(this.savePoint_800d5598, i -> new SavePointRenderData44());
  }
  private boolean hasSavePoint_800d5620;
  private final Vector3f savePointPos_800d5622 = new Vector3f();

  private final SavePointRenderData44[] savePoint_800d5630 = new SavePointRenderData44[32];
  {
    Arrays.setAll(this.savePoint_800d5630, i -> new SavePointRenderData44());
  }
  private final Model124 savePointModel_800d5eb0 = new Model124("Save point");

  private final SmokeParticleEffect smokePlumeEffect_800d5fd8 = new SmokeParticleEffect();

  private final int[] texPages_800d6050 = new int[12];
  private final int[] cluts_800d6068 = new int[12];

  private final Vector3f[] footprintQuadVertices_800d6b7c = {
    new Vector3f(-10.0f, 0.0f, -22.0f),
    new Vector3f( 10.0f, 0.0f, -22.0f),
    new Vector3f(-10.0f, 0.0f,  22.0f),
    new Vector3f( 10.0f, 0.0f,  22.0f),
    new Vector3f(-12.0f, 0.0f, - 8.0f),
    new Vector3f(- 2.0f, 0.0f, - 8.0f),
    new Vector3f(-12.0f, 0.0f,   8.0f),
    new Vector3f(- 2.0f, 0.0f,   8.0f),
    new Vector3f(  2.0f, 0.0f, - 8.0f),
    new Vector3f( 12.0f, 0.0f, - 8.0f),
    new Vector3f(  2.0f, 0.0f,   8.0f),
    new Vector3f( 12.0f, 0.0f,   8.0f),
  };
  private final int[] orthoDustUs_800d6bdc = {96, 112, 64, 0};
  private final int[] dustTextureWidths_800d6bec = {15, 15, 31, 23};
  private final int[] dustTextureHeights_800d6bfc = {31, 31, 31, 23};
  private final int[] particleFadeDelay_800d6c0c = {120, 0, 120};


  private final Vector3f savePointV0_800d6c28 = new Vector3f(-24.0f, -32.0f,  24.0f);
  private final Vector3f savePointV1_800d6c30 = new Vector3f( 24.0f, -32.0f,  24.0f);
  private final Vector3f savePointV2_800d6c38 = new Vector3f(-24.0f, -32.0f, -24.0f);
  private final Vector3f savePointV3_800d6c40 = new Vector3f( 24.0f, -32.0f, -24.0f);
  private final Vector3f _800d6c48 = new Vector3f(0.0f, -24.0f, 0.0f);
  private final Vector3f _800d6c50 = new Vector3f(0.0f,  24.0f, 0.0f);
  private final int[] _800d6c58 = {0x200, 0x800, 0x400, 0x800, 0x100, 0x61c, 0x960, 0xe10};
  private final int[] _800d6c78 = {6, 4, 5, 3, 5, 7, 5, 6};
  private final int[] savePointFloatiesRotations_800d6c88 = {-14, -55, 22, 16, -28, -14, 24, 27};
  private final int dartArrowU_800d6ca8 = 0;
  private final int dartArrowV_800d6cac = 96;
  private final int doorArrowU_800d6cb0 = 128;
  private final int doorArrowV_800d6cb4 = 0;
  private final Vector3f bottom_800d6cb8 = new Vector3f();
  private final Vector3f top_800d6cc0 = new Vector3f(0.0f, 40.0f, 0.0f);
  private final int[] _800d6cc8 = {206, 206, 207, 208};
  private final int[] _800d6cd8 = {992, 992, 976};
  private final int[] _800d6ce4 = {208, 207, 8};
  private final Translucency[] miscTextureTransModes_800d6cf0 = {Translucency.B_PLUS_F, Translucency.B_PLUS_F, Translucency.B_PLUS_F, Translucency.B_PLUS_F, Translucency.B_PLUS_QUARTER_F, Translucency.B_PLUS_F, Translucency.B_PLUS_F, Translucency.B_MINUS_F, Translucency.B_MINUS_F, Translucency.B_PLUS_F, Translucency.B_PLUS_F};

  private AnmFile savepointAnm1;
  private AnmFile savepointAnm2;
  private CContainer savepointTmd;
  private TmdAnimationFile savepointAnimation;
  private CContainer dustTmd;
  private TmdAnimationFile dustAnimation;

  private final String bigArrow_800d7620 = "big_arrow.tim";
  private final String smallArrow_800d7c60 = "small_arrow.tim";
  private final String savepoint_800d7ee0 = "savepoint.tim";
  private final String tim_800d8520 = "800d8520.tim";
  private final String tim_800d85e0 = "800d85e0.tim";
  private final String savepointBigCircle_800d8720 = "savepoint_big_circle.tim";
  private final String dust_800d8960 = "dust.tim";
  private final String leftFoot_800d8ba0 = "left_foot.tim";
  private final String rightFoot_800d8ce0 = "right_foot.tim";
  private final String smoke1_800d8e20 = "smoke_1.tim";
  private final String smoke2_800d9060 = "smoke_2.tim";

  public static final ShopStruct40[] shops_800f4930 = {
    new ShopStruct40(0, 1, 28, 47, 63, 77, 103, 106, 107, 108, 112, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(1, 203, 222, 205, 206, 198, 208, 223, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 21, 83, 89, 104, 105, 106, 111, 156, 149, 150, 151, 157, 255, 255, 255, 255),
    new ShopStruct40(1, 203, 229, 222, 205, 206, 199, 209, 223, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 2, 29, 78, 94, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(1, 203, 229, 222, 202, 214, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 14, 48, 97, 103, 104, 105, 106, 107, 108, 109, 111, 112, 156, 255, 255, 255),
    new ShopStruct40(1, 203, 249, 229, 222, 205, 206, 201, 216, 223, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 64, 84, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(1, 203, 229, 222, 205, 206, 220, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 22, 41, 58, 107, 109, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(1, 203, 249, 229, 222, 205, 206, 201, 210, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 3, 30, 35, 49, 65, 79, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(1, 203, 249, 229, 222, 205, 223, 195, 209, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 15, 80, 95, 110, 115, 118, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(1, 231, 229, 222, 204, 207, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 9, 23, 42, 54, 59, 81, 85, 98, 105, 108, 110, 74, 255, 255, 255, 255),
    new ShopStruct40(1, 231, 249, 229, 222, 204, 205, 206, 194, 207, 223, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 36, 113, 114, 119, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(1, 231, 229, 249, 222, 205, 206, 202, 216, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 24, 37, 55, 60, 117, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(1, 231, 249, 229, 222, 205, 206, 223, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 5, 33, 43, 99, 113, 114, 118, 119, 120, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(1, 231, 249, 229, 222, 223, 212, 215, 217, 218, 200, 225, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 115, 116, 117, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(1, 231, 249, 229, 222, 205, 206, 212, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 6, 25, 38, 44, 51, 52, 56, 61, 67, 68, 69, 113, 114, 115, 116, 117),
    new ShopStruct40(1, 231, 249, 229, 222, 204, 205, 206, 212, 217, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(3, 203, 222, 194, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 4, 10, 16, 31, 50, 66, 116, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(1, 231, 249, 229, 222, 206, 223, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 142, 143, 146, 148, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(1, 203, 206, 195, 223, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 153, 154, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(1, 203, 229, 222, 205, 206, 210, 197, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 11, 17, 51, 52, 68, 69, 106, 108, 110, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(1, 231, 229, 249, 222, 204, 205, 206, 223, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(3, 203, 229, 222, 205, 218, 207, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 203, 229, 222, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
    new ShopStruct40(0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255),
  };

  /** Related to indicator being disabled for cutscenes/conversations */
  private boolean indicatorDisabledForCutscene_800f64ac;

  private final AlertIndicator14 alertIndicatorMetrics_800f64b0 = new AlertIndicator14(-12, 12, -12, 12, 24, 48, 64, 88);

  private int playerPositionRestoreMode_800f7e24;

  private final ChapterStruct08[] submapChapterDestinations_800f7e2c = {
    new ChapterStruct08(0, 0),
    new ChapterStruct08(709, 0),
    new ChapterStruct08(710, 0),
    new ChapterStruct08(745, 58),
  };
  private boolean _800f7e4c;
  private int scriptSetOffsetMode_800f7e50;
  /**
   * <ul>
   *   <li>0x1 - disable encounters</li>
   * </ul>
   */
  private int submapFlags_800f7e54;

  private final float[] oldRotations_800f7f6c = new float[8];
  private boolean firstMovement;

  private int snowState_800f9e60;

  private int lawPodTrailSegmentCount_800f9e78;

  private final LawPodTrailData18[] lawPodTrailSegments_800f9e7c = new LawPodTrailData18[8];
  private int momentaryIndicatorTicks_800f9e9c;

  private int _800f9ea0;

  private int submapEffectsLoadMode_800f9ea8;
  /**
   * <ul>
   *   <li>-1 - unload</li>
   *   <li>0 - not loaded</li>
   *   <li>1 - textures loaded</li>
   *   <li>2 - fully loaded</li>
   * </ul>
   */
  private int submapEffectsState_800f9eac;
  private final String[] miscTextures_800f9eb0 = {
    this.bigArrow_800d7620,
    this.smallArrow_800d7c60,
    this.savepoint_800d7ee0,
    this.tim_800d8520,
    this.tim_800d85e0,
    this.savepointBigCircle_800d8720,
    this.dust_800d8960,
    this.leftFoot_800d8ba0,
    this.rightFoot_800d8ce0,
    this.smoke1_800d8e20,
    this.smoke2_800d9060,
  };

  private final MapIndicator mapIndicator = new MapIndicator();

  @Override
  public void restoreMusicAfterMenu() {
    this.submap.startMusic();
  }

  @Override
  public Function<RunningScript, FlowControl>[] getScriptFunctions() {
    final Function<RunningScript, FlowControl>[] functions = new Function[1024];
    functions[12] = this::scriptRestoreCharDataVitals;
    functions[13] = this::scriptClearStatusEffects;
    functions[14] = this::scriptCloneCharacterData;
    functions[15] = this::scriptSetCharAddition;

    functions[18] = this::scriptGetCharAddition;
    functions[19] = this::scriptMaxOutDartDragoon;

    functions[96] = this::scriptSelfLoadSobjModelAndAnimation;
    functions[97] = this::scriptSelfLoadSobjAnimation;
    functions[98] = this::scriptSelfGetSobjAnimation;
    functions[99] = this::FUN_800df1f8;
    functions[100] = this::FUN_800df228;
    functions[101] = this::scriptSetModelPosition;
    functions[102] = this::scriptReadModelPosition;
    functions[103] = this::scriptSetModelRotate;
    functions[104] = this::scriptReadModelRotate;
    functions[105] = this::scriptSelfFacePoint;
    functions[106] = this::FUN_800df410;
    functions[107] = this::FUN_800df440;
    functions[108] = this::FUN_800df488;
    functions[109] = this::FUN_800df4d0;
    functions[110] = this::FUN_800df500;
    functions[111] = this::FUN_800df530;
    functions[112] = this::FUN_800df560;
    functions[113] = this::scriptSelfEnableTextureAnimation;
    functions[114] = this::FUN_800df590;
    functions[115] = this::scriptSelfDisableTextureAnimation;
    functions[116] = this::FUN_800df620;
    functions[117] = this::scriptSelfAttachCameraToSobj;
    functions[118] = this::scriptSelfIsCameraAttached;
    functions[119] = this::scriptSetCameraPos;
    functions[120] = this::scriptRotateSobj;
    functions[121] = this::scriptRotateSobjAbsolute;
    functions[122] = this::FUN_800df904;
    functions[123] = this::scriptMovePlayer;
    functions[124] = this::scriptFacePlayer;
    functions[125] = this::FUN_800df9a8;
    functions[126] = this::scriptGetCurrentSubmapId;
    functions[127] = this::scriptSelfGetSobjNobj;

    functions[198] = this::scriptAddSobjTextbox;

    functions[256] = this::scriptMapTransition;
    functions[257] = this::scriptGetCameraOffset;
    functions[258] = this::scriptSetCameraOffset;
    functions[259] = this::scriptGetCollisionAndTransitionInfo;
    functions[260] = this::FUN_800e69e8;
    functions[261] = this::scriptBlockCollisionPrimitive;
    functions[262] = this::scriptUnblockCollisionPrimitive;
    functions[263] = this::scriptSetEnvForegroundPosition;
    functions[264] = this::scriptSetModeParamForNextCallToScriptSetCameraOffsetOrHideSubmapForegroundObject;
    functions[265] = this::scriptGetSetEncountersDisabled;
    functions[266] = this::FUN_800e6aa0;
    functions[267] = this::scriptGetCollisionPrimitivePos;
    functions[268] = this::FUN_800e6bd8;
    functions[269] = this::FUN_800e6be0;
    functions[270] = this::FUN_800e6cac;
    functions[271] = this::scriptStartFmv;

    functions[288] = this::scriptSelfHideModelPart;
    functions[289] = this::scriptSelfShowModelPart;
    functions[290] = this::scriptSelfFaceCamera;
    functions[291] = this::scriptScaleXyz;
    functions[292] = this::scriptScaleUniform;
    functions[293] = this::scriptSetModelZOffset;
    functions[294] = this::scriptSelfSetSobjFlag;
    functions[295] = this::FUN_800dfd10;
    functions[296] = this::FUN_800de334;
    functions[297] = this::FUN_800de4b4;
    functions[298] = this::scriptShowAlertIndicator;
    functions[299] = this::scriptHideAlertIndicator;
    functions[300] = this::FUN_800dfd48;
    functions[301] = this::FUN_800e05c8;
    functions[302] = this::FUN_800e05f0;
    functions[303] = this::scriptEnableSobjFlatLight;
    functions[304] = this::scriptDisableSobjFlatLight;
    functions[305] = this::scriptSetSobjCollision1;
    functions[306] = this::scriptGetSobjCollidedWith1;
    functions[307] = this::scriptSetSobjCollision2;
    functions[308] = this::scriptGetSobjCollidedWith2;
    functions[309] = this::scriptSetAmbientColour;
    functions[310] = this::scriptResetAmbientColour;
    functions[311] = this::scriptEnableShadow;
    functions[312] = this::scriptDisableShadow;
    functions[313] = this::scriptSetShadowSize;
    functions[314] = this::scriptSetShadowOffset;
    functions[315] = this::scriptCheckPlayerCollision;
    functions[316] = this::scriptGetPlayerMovement;
    functions[317] = this::scriptSwapShadowTexture;
    functions[318] = this::scriptSetSobjPlayerCollisionMetrics;
    functions[319] = this::FUN_800e0cb8;

    functions[672] = this::scriptLoadSobjModelAndAnimation;
    functions[673] = this::scriptLoadSobjAnimation;
    functions[674] = this::scriptGetSobjAnimation;
    functions[675] = this::scriptToggleAnimationDisabled;
    functions[676] = this::scriptIsAnimationFinished;
    functions[677] = this::scriptFacePoint;
    functions[678] = this::scriptSetSobjHidden;
    functions[679] = this::FUN_800de668;
    functions[680] = this::FUN_800de944;
    functions[681] = this::FUN_800e00cc;
    functions[682] = this::FUN_800e0148;
    functions[683] = this::FUN_800e01bc;
    functions[684] = this::scriptEnableTextureAnimation;
    functions[685] = this::FUN_800e0204;
    functions[686] = this::scriptDisableTextureAnimation;
    functions[687] = this::FUN_800e02c0;
    functions[688] = this::scriptAttachCameraToSobj;
    functions[689] = this::FUN_800deba0;
    functions[690] = this::scriptGetSobjNobj;
    functions[691] = this::scriptHideModelPart;
    functions[692] = this::scriptShowModelPart;
    functions[693] = this::scriptFaceCamera;
    functions[694] = this::scriptSetSobjFlag;
    functions[695] = this::scriptGetSobjFlag;
    functions[696] = this::loadInterpolatedSobjAnimation;
    functions[697] = this::loadUninterpolatedSobjAnimation;
    functions[698] = this::FUN_800e0184;
    functions[699] = this::scriptSetChapterTitleCardReadyToRender;
    functions[700] = this::scriptGetChapterTitleCardAnimationComplete;
    functions[701] = this::scriptLoadChapterTitleCard;
    functions[702] = this::scriptIsChapterTitleCardLoaded;
    functions[703] = this::scriptSetTitleCardAnimationPauseTicks;

    functions[768] = this::scriptSelfInitTmdDust;
    functions[769] = this::scriptInitTmdDust;
    functions[770] = this::scriptAllocateSmokePlumeEffectData;
    functions[771] = this::scriptInitFootprints;
    functions[772] = this::scriptAddSavePoint;
    functions[773] = this::scriptInitOrthoDust;
    functions[774] = this::FUN_800f2780;
    functions[775] = this::scriptReinitializeSmokePlumeForIntermittentBursts;
    functions[776] = this::scriptInitSnowParticleData;
    functions[777] = this::FUN_800f1eb8;
    functions[778] = this::scriptAllocateTriangleIndicatorArray;
    functions[779] = this::FUN_800f1b64;
    functions[780] = this::FUN_800f26c8;
    functions[781] = this::FUN_800f1d0c;
    functions[782] = this::scriptAllocateSmokeCloudData;
    functions[783] = this::scriptDeallocateSmokeCloudDataAndEffect;
    functions[784] = this::scriptSetSmokeCloudEffectStateToDontTick;
    functions[785] = this::scriptSetFootprintsInstantiationInterval;
    functions[786] = this::scriptInitLawPodTrail;
    functions[787] = this::scriptChangeFootprintsMode;
    functions[788] = this::FUN_800f2554;
    functions[789] = this::scriptDeallocateLawPodTrail;
    functions[790] = this::scriptAllocateUnusedSmokeEffectData;
    return functions;
  }

  @Override
  public boolean allowsWidescreen() {
    return false;
  }

  @Override
  public boolean allowsHighQualityProjection() {
    return false;
  }

  @ScriptDescription("Adds a textbox to a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "index", description = "The textbox index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "submapObjectIndex", description = "The submap object, but may also have the flag 0x1000 set (unknown meaning)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "packedData", description = "Unknown data, 3 nibbles")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "width", description = "The textbox width")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "height", description = "The textbox height")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.STRING, name = "text", description = "The textbox text")
  @Method(0x80025218L)
  private FlowControl scriptAddSobjTextbox(final RunningScript<?> script) {
    if(script.params_20[2].get() == 0) {
      return FlowControl.CONTINUE;
    }

    final int textboxIndex = script.params_20[0].get();
    final int textType = textboxTextType_80052ba8[script.params_20[2].get() >>> 8 & 0xf];
    clearTextbox(textboxIndex);

    final Textbox4c textbox = textboxes_800be358[textboxIndex];
    textbox.type_04 = TextboxType.fromInt(textboxMode_80052b88[script.params_20[2].get() >>> 4 & 0xf]);
    textbox.renderBorder_06 = renderBorder_80052b68[script.params_20[2].get() & 0xf];
    textbox.x_14 = 0;
    textbox.y_16 = 0;
    textbox.chars_18 = script.params_20[3].get() + 1;
    textbox.lines_1a = script.params_20[4].get() + 1;
    clearTextboxText(textboxIndex);

    final TextboxText84 textboxText = textboxText_800bdf38[textboxIndex];
    textboxText.type_04 = textType;
    textboxText.str_24 = LodString.fromParam(script.params_20[5]);

    if(textType == 1 && (script.params_20[1].get() & 0x1000) > 0) {
      textboxText.flags_08 |= 0x20;
    }

    //LAB_80025370
    //LAB_80025374
    if(textType == 3) {
      textboxText.selectionIndex_6c = -1;
    }

    //LAB_800253a4
    if(textType == 4) {
      textboxText.flags_08 |= TextboxText84.HAS_NAME;
    }

    //LAB_800253d4
    textboxText.flags_08 |= TextboxText84.SHOW_ARROW;
    textboxText.chars_58 = new TextboxChar08[textboxText.chars_1c * (textboxText.lines_1e + 1)];
    Arrays.setAll(textboxText.chars_58, i -> new TextboxChar08());
    this.positionSobjTextbox(textboxIndex, script.params_20[1].get());

    if(textType == 2) {
      textbox._38 = textbox.x_14;
      textbox._3c = textbox.y_16;
      textbox.x_14 = textbox.currentX_28;
      textbox.y_16 = textbox.currentY_2c;
      textbox.flags_08 |= 0x2;
    }

    //LAB_80025494
    //LAB_80025498
    return FlowControl.CONTINUE;
  }

  /** Calculates a good position to place a textbox for a specific sobj */
  @Method(0x80028938L)
  private void positionSobjTextbox(final int textboxIndex, final int sobjIndex) {
    final Textbox4c textbox = textboxes_800be358[textboxIndex];
    final TextboxText84 textboxText = textboxText_800bdf38[textboxIndex];

    this.cacheSobjScreenBounds(sobjIndex);
    final SubmapCaches80 caches = this.caches_800c68e8;
    final float offsetX = caches.bottomMiddle_70.x - caches.bottomLeft_68.x;
    final float offsetY = (caches.bottomMiddle_70.y - caches.topMiddle_78.y) / 2;
    textbox.currentX_28 = caches.bottomMiddle_70.x;
    textbox.currentY_2c = caches.bottomMiddle_70.y - offsetY;
    final int textWidth = textbox.chars_18 * 9 / 2;
    final int textHeight = textbox.lines_1a * 6;

    final int width;
    if(engineState_8004dd20 != EngineStateEnum.SUBMAP_05) {
      width = 320;
    } else {
      width = 360;
    }

    //LAB_80028a20
    if(textboxText.chars_1c >= 17) {
      if(textbox.currentY_2c >= 121) {
        //LAB_80028acc
        final int x = width / 2;
        final float y = textbox.currentY_2c - offsetY - textHeight;
        textbox.x_14 = x;
        textbox.y_16 = y;

        textboxText.x_14 = x;
        textboxText.y_16 = y;
        textboxText._18 = textboxText.x_14 - textboxText.chars_1c * 4.5f;
        textboxText._1a = textboxText.y_16 - textboxText.lines_1e * 6.0f;
        return;
      }

      final int x = width / 2;
      final float y = textbox.currentY_2c + offsetY + textHeight;
      textbox.x_14 = x;
      textbox.y_16 = y;

      textboxText.x_14 = x;
      textboxText.y_16 = y;
      textboxText._18 = textboxText.x_14 - textboxText.chars_1c * 4.5f;
      textboxText._1a = textboxText.y_16 - textboxText.lines_1e * 6.0f;
      return;
    }

    //LAB_80028b38
    float y = textbox.currentY_2c - offsetY - textHeight;
    if(textboxFits(textboxIndex, textbox.currentX_28, y)) {
      textbox.x_14 = textbox.currentX_28;
      textbox.y_16 = y;

      textboxText.x_14 = textbox.currentX_28;
      textboxText.y_16 = y;
      textboxText._18 = textboxText.x_14 - textboxText.chars_1c * 4.5f;
      textboxText._1a = textboxText.y_16 - textboxText.lines_1e * 6.0f;
      return;
    }

    //LAB_80028bc4
    y = textbox.currentY_2c + offsetY + textHeight;
    if(textboxFits(textboxIndex, textbox.currentX_28, y)) {
      textbox.x_14 = textbox.currentX_28;
      textbox.y_16 = y;

      textboxText.x_14 = textbox.currentX_28;
      textboxText.y_16 = y;
      textboxText._18 = textboxText.x_14 - textboxText.chars_1c * 4.5f;
      textboxText._1a = textboxText.y_16 - textboxText.lines_1e * 6.0f;
      return;
    }

    //LAB_80028c44
    if(width / 2.0f < textbox.currentX_28) {
      //LAB_80028d58
      final float x = textbox.currentX_28 - offsetX - textWidth;
      y = textbox.currentY_2c - offsetY - textHeight / 2.0f;
      if(textboxFits(textboxIndex, x, y)) {
        textbox.x_14 = x;
        textbox.y_16 = y;

        textboxText.x_14 = x;
        textboxText.y_16 = y;
        textboxText._18 = textboxText.x_14 - textboxText.chars_1c * 4.5f;
        textboxText._1a = textboxText.y_16 - textboxText.lines_1e * 6.0f;
        return;
      }

      //LAB_80028df0
      y = textbox.currentY_2c + offsetY + textHeight / 2.0f;
      if(textboxFits(textboxIndex, x, y)) {
        textbox.x_14 = x;
        textbox.y_16 = y;

        textboxText.x_14 = x;
        textboxText.y_16 = y;
        textboxText._18 = textboxText.x_14 - textboxText.chars_1c * 4.5f;
        textboxText._1a = textboxText.y_16 - textboxText.lines_1e * 6.0f;
        return;
      }
    } else {
      final float x = textbox.currentX_28 + offsetX + textWidth;
      y = textbox.currentY_2c - offsetY - textHeight / 2.0f;
      if(textboxFits(textboxIndex, x, y)) {
        textbox.x_14 = x;
        textbox.y_16 = y;

        textboxText.x_14 = x;
        textboxText.y_16 = y;
        textboxText._18 = textboxText.x_14 - textboxText.chars_1c * 4.5f;
        textboxText._1a = textboxText.y_16 - textboxText.lines_1e * 6.0f;
        return;
      }

      //LAB_80028ce4
      y = textbox.currentY_2c + offsetY + textHeight / 2.0f;
      if(textboxFits(textboxIndex, x, y)) {
        textbox.x_14 = x;
        textbox.y_16 = y;

        textboxText.x_14 = x;
        textboxText.y_16 = y;
        textboxText._18 = textboxText.x_14 - textboxText.chars_1c * 4.5f;
        textboxText._1a = textboxText.y_16 - textboxText.lines_1e * 6.0f;
        return;
      }
    }

    //LAB_80028e68
    final float x;
    if(width / 2.0f >= textbox.currentX_28) {
      x = textbox.currentX_28 + offsetX + textWidth;
    } else {
      //LAB_80028e8c
      x = textbox.currentX_28 - offsetX - textWidth;
    }

    //LAB_80028e9c
    calculateAppropriateTextboxBounds(textboxIndex, x, textbox.currentY_2c + offsetY + textHeight);

    //LAB_80028ef0
  }

  /** Pulled from BPE segment */
  @Method(0x8002aae8L)
  private void renderEnvironmentAndHandleTransitions() {
    switch(submapEnvState_80052c44) {
      case RENDER_AND_CHECK_TRANSITIONS_0:
        this.renderEnvironment();

      case CHECK_TRANSITIONS_1_2:
        if((this.submapFlags_800f7e54 & 0x1) == 0) {
          // If an encounter should start
          if(this.handleEncounters()) {
            this.mapTransition(-1, 0);
          }
        }

        //LAB_8002abdc
        //LAB_8002abe0
        // Handle map transitions
        final int collisionAndTransitionInfo = this.collisionGeometry_800cbe08.getCollisionAndTransitionInfo(collidedPrimitiveIndex_80052c38);
        if((collisionAndTransitionInfo & 0x10) != 0) {
          this.mapTransition(collisionAndTransitionInfo >>> 22, collisionAndTransitionInfo >>> 16 & 0x3f);
        }
        break;

      case RENDER_AND_UNLOAD_4_5:
        this.renderEnvironment();

      case UNLOAD_3:
        this.unloadSmap();
        submapEnvState_80052c44 = SubmapEnvState.CHECK_TRANSITIONS_1_2;
        break;
    }
  }

  @Method(0x800d9b08L)
  private void restoreCharDataVitals(final int charId) {
    loadCharacterStats();

    if(charId >= 0) {
      final ActiveStatsa0 stats = stats_800be5f8[charId];
      final CharacterData2c charData = gameState_800babc8.charData_32c[charId];
      charData.hp_08 = stats.maxHp_66;
      charData.mp_0a = stats.maxMp_6e;
    } else {
      //LAB_800d9b70
      //LAB_800d9b84
      for(int charSlot = 0; charSlot < 9; charSlot++) {
        final ActiveStatsa0 stats = stats_800be5f8[charSlot];
        final CharacterData2c charData = gameState_800babc8.charData_32c[charSlot];
        charData.hp_08 = stats.maxHp_66;
        charData.mp_0a = stats.maxMp_6e;
      }
    }
  }

  @ScriptDescription("Restore vitals for all characters")
  @Method(0x800d9bc0L)
  private FlowControl scriptRestoreCharDataVitals(final RunningScript<?> script) {
    this.restoreCharDataVitals(-1);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Clears status effects for all characters")
  @Method(0x800d9bf4L)
  private FlowControl scriptClearStatusEffects(final RunningScript<?> script) {
    //LAB_800d9c04
    for(int i = 0; i < 9; i++) {
      gameState_800babc8.charData_32c[i].status_10 = 0;
    }

    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Overwrite one character's CharData with that of another character (also restores the destination character's vitals)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sourceCharId", description = "The source character")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "destCharId", description = "The destination character")
  @Method(0x800d9c1cL)
  private FlowControl scriptCloneCharacterData(final RunningScript<?> script) {
    //LAB_800d9c78
    gameState_800babc8.charData_32c[script.params_20[1].get()].set(gameState_800babc8.charData_32c[script.params_20[0].get()]);
    this.restoreCharDataVitals(script.params_20[1].get());
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Set a character's addition")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "charId", description = "The character ID")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "additionId", description = "The addition ID")
  @Method(0x800d9ce4L)
  private FlowControl scriptSetCharAddition(final RunningScript<?> script) {
    gameState_800babc8.charData_32c[script.params_20[0].get()].selectedAddition_19 = script.params_20[1].get();
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Get a character's addition")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "charId", description = "The character ID")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "additionId", description = "The addition ID")
  @Method(0x800d9d20L)
  private FlowControl scriptGetCharAddition(final RunningScript<?> script) {
    script.params_20[1].set(gameState_800babc8.charData_32c[script.params_20[0].get()].selectedAddition_19);
    return FlowControl.CONTINUE;
  }

  /** Called when Dart is given Divine Dragon spirit */
  @ScriptDescription("Maxes out Dart's Dragoon and fully restores his HP/MP/SP")
  @Method(0x800d9d60L)
  private FlowControl scriptMaxOutDartDragoon(final RunningScript<?> script) {
    if(gameState_800babc8.charData_32c[0].dlevelXp_0e < 63901) {
      gameState_800babc8.charData_32c[0].dlevelXp_0e = 63901;
    }

    //LAB_800d9d90
    gameState_800babc8.charData_32c[0].dlevel_13 = 5;

    this.restoreVitalsAndSp(0);
    return FlowControl.CONTINUE;
  }

  @Method(0x800d9dc0L)
  private void restoreVitalsAndSp(final int charIndex) {
    gameState_800babc8.charData_32c[charIndex].sp_0c = 500;
    this.restoreCharDataVitals(-1);
  }

  @Method(0x800da524L)
  private void renderSmapShadow(final Model124 model) {
    GsInitCoordinate2(model.coord2_14, shadowModel_800bda10.coord2_14);

    shadowModel_800bda10.zOffset_a0 = model.zOffset_a0 + 16;
    shadowModel_800bda10.coord2_14.transforms.scale.set(model.shadowSize_10c).div(64.0f);

    shadowModel_800bda10.coord2_14.coord.rotationXYZ(shadowModel_800bda10.coord2_14.transforms.rotate);
    shadowModel_800bda10.coord2_14.coord.scaleLocal(shadowModel_800bda10.coord2_14.transforms.scale);
    shadowModel_800bda10.coord2_14.coord.transfer.set(model.shadowOffset_118);

    final ModelPart10 modelPart = shadowModel_800bda10.modelParts_00[0];
    final GsCOORDINATE2 partCoord = modelPart.coord2_04;

    partCoord.transforms.rotate.zero();
    partCoord.transforms.trans.zero();

    partCoord.coord.rotationZYX(partCoord.transforms.rotate);
    partCoord.coord.transfer.set(partCoord.transforms.trans);

    final MV lw = new MV();
    GsGetLw(partCoord, lw);

    RENDERER
      .queueModel(modelPart.obj, lw)
      .screenspaceOffset(this.screenOffset_800cb568.x + 8, -this.screenOffset_800cb568.y)
      .lightDirection(lightDirectionMatrix_800c34e8)
      .lightColour(lightColourMatrix_800c3508)
      .backgroundColour(GTE.backgroundColour);

    partCoord.flg--;
  }

  @Method(0x800daa3cL)
  private void renderSmapModel(final Model124 model) {
    zOffset_1f8003e8 = model.zOffset_a0;
    tmdGp0Tpage_1f8003ec = model.tpage_108;

    //LAB_800daaa8
    final MV lw = new MV();
    final MV ls = new MV();
    for(int i = 0; i < model.modelParts_00.length; i++) {
      if((model.partInvisible_f4 & 1L << i) == 0) {
        final ModelPart10 dobj2 = model.modelParts_00[i];

        GsGetLws(dobj2.coord2_04, lw, ls);
        GsSetLightMatrix(lw);
        GTE.setTransforms(ls);
        Renderer.renderDobj2(dobj2, false, 0);

        if(dobj2.obj != null) { //TODO remove me
          GsGetLw(dobj2.coord2_04, lw);

          RENDERER.queueModel(dobj2.obj, lw)
            .screenspaceOffset(this.screenOffset_800cb568.x + 8, -this.screenOffset_800cb568.y)
            .lightDirection(lightDirectionMatrix_800c34e8)
            .lightColour(lightColourMatrix_800c3508)
            .backgroundColour(GTE.backgroundColour);
        }
      }
    }

    //LAB_800dab34
    if(model.shadowType_cc != 0) {
      this.renderSmapShadow(model);
    }

    //LAB_800dab4c
  }

  @Override
  @Method(0x800de004L)
  public void modelLoaded(final Model124 model, final CContainer cContainer) {
    if(cContainer.ext_04 == null) {
      //LAB_800de120
      model.smallerStructPtr_a4 = null;
      return;
    }

    final SmallerStruct smallerStruct = new SmallerStruct();
    model.smallerStructPtr_a4 = smallerStruct;

    smallerStruct.tmdExt_00 = cContainer.ext_04;

    //LAB_800de05c
    for(int i = 0; i < 4; i++) {
      smallerStruct.tmdSubExtensionArr_20[i] = smallerStruct.tmdExt_00.tmdSubExtensionArr_00[i];

      if(smallerStruct.tmdSubExtensionArr_20[i] == null) {
        smallerStruct.uba_04[i] = false;
      } else {
        smallerStruct.sa_08[i] = 0;
        smallerStruct.sa_10[i] = 0;
        smallerStruct.sa_18[i] = smallerStruct.tmdSubExtensionArr_20[i].s_02;
        smallerStruct.uba_04[i] = smallerStruct.sa_18[i] != -1;
      }

      //LAB_800de108
    }

    //LAB_800de124
  }

  @Method(0x800de138L)
  private void FUN_800de138(final Model124 model, final int index) {
    final SmallerStruct smallerStruct = model.smallerStructPtr_a4;

    if(smallerStruct.tmdSubExtensionArr_20[index] == null) {
      smallerStruct.uba_04[index] = false;
      return;
    }

    //LAB_800de164
    smallerStruct.sa_08[index] = 0;
    smallerStruct.sa_10[index] = 0;
    smallerStruct.sa_18[index] = smallerStruct.tmdSubExtensionArr_20[index].s_02;
    smallerStruct.uba_04[index] = smallerStruct.sa_18[index] != -1;
  }

  @ScriptDescription("Moves the player")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "Delta X position")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "Delta Y position")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "Delta Z position")
  @Method(0x800de1d0L)
  private FlowControl scriptMovePlayer(final RunningScript<SubmapObject210> script) {
    final short deltaX = (short)script.params_20[0].get();
    final short deltaY = (short)script.params_20[1].get();
    final short deltaZ = (short)script.params_20[2].get();

    if(deltaX != 0 || deltaY != 0 || deltaZ != 0) {
      final Vector3f deltaMovement = new Vector3f();
      final Vector3f worldspaceDeltaMovement = new Vector3f();

      //LAB_800de218
      final SubmapObject210 player = script.scriptState_04.innerStruct_00;
      final Model124 playerModel = player.model_00;

      deltaMovement.set(deltaX, deltaY, deltaZ);
      GTE.setTransforms(worldToScreenMatrix_800c3548);
      this.transformToWorldspace(worldspaceDeltaMovement, deltaMovement);

      final int collidedPrimitiveIndex = this.collisionGeometry_800cbe08.handleCollisionAndMovement(player.sobjIndex_12e != 0, playerModel.coord2_14.coord.transfer, worldspaceDeltaMovement);
      if(collidedPrimitiveIndex >= 0) {
        if(this.isWalkable(collidedPrimitiveIndex)) {
          playerModel.coord2_14.coord.transfer.y = worldspaceDeltaMovement.y;
          worldspaceDeltaMovement.y = 0;

          player.movementTicksTotal = 2 / vsyncMode_8007a3b8;
          player.movementTicks_144 = 0;
          player.movementStart.set(playerModel.coord2_14.coord.transfer);
          player.movementStart.add(worldspaceDeltaMovement, player.movementDestination_138);
          script.scriptState_04.setTempTicker(this::tickBasicMovement);
        }

        //LAB_800de2c8
        player.collidedPrimitiveIndex_16c = collidedPrimitiveIndex;
      }

      //LAB_800de2cc
      player.us_170 = 0;
//      script.scriptState_04.setTempTicker(this::tickBasicMovement);
      this.caches_800c68e8.playerPos_00.set(worldspaceDeltaMovement);
    }

    //LAB_800de318
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Unknown, related to triangle indicators")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "collisionPrimitiveIndex")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "p1")
  @Method(0x800de334L)
  private FlowControl FUN_800de334(final RunningScript<?> script) {
    this.collisionGeometry_800cbe08.getMiddleOfCollisionPrimitive(script.params_20[0].get(), this.playerModel_800c6748.coord2_14.coord.transfer);

    final MV ls = new MV();
    GsGetLs(this.playerModel_800c6748.coord2_14, ls);
    GTE.setTransforms(ls);
    GTE.perspectiveTransform(0, 0, 0);
    final float x = GTE.getScreenX(2);
    final float y = GTE.getScreenY(2);

    //LAB_800de438
    final TriangleIndicator140 indicator = this.triangleIndicator_800c69fc;
    for(int i = 0; i < 20; i++) {
      if(indicator._18[i] == -1) {
        indicator.x_40[i] = x;
        indicator.y_68[i] = y;
        indicator._18[i] = (short)script.params_20[1].get();
        indicator.screenOffsetX_90[i] = this.screenOffset_800cb568.x;
        indicator.screenOffsetY_e0[i] = this.screenOffset_800cb568.y;
        break;
      }
    }

    //LAB_800de49c
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Unknown, related to triangle indicators")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT_ARRAY, name = "p0")
  @Method(0x800de4b4L)
  private FlowControl FUN_800de4b4(final RunningScript<?> script) {
    final MV ls = new MV();

    final Param ints = script.params_20[0];
    int s0 = 0;

    //LAB_800de4f8
    while(ints.array(s0).get() != -1) {
      this.collisionGeometry_800cbe08.getMiddleOfCollisionPrimitive(ints.array(s0++).get(), this.playerModel_800c6748.coord2_14.coord.transfer);

      GsGetLs(this.playerModel_800c6748.coord2_14, ls);
      GTE.setTransforms(ls);
      GTE.perspectiveTransform(0, 0, 0);
      final float x = GTE.getScreenX(2);
      final float y = GTE.getScreenY(2);

      //LAB_800de5d4
      for(int i = 0; i < 20; i++) {
        final TriangleIndicator140 indicator = this.triangleIndicator_800c69fc;

        if(indicator._18[i] == -1) {
          indicator.x_40[i] = x;
          indicator.y_68[i] = y;
          indicator._18[i] = (short)ints.array(s0).get();
          indicator.screenOffsetX_90[i] = this.screenOffset_800cb568.x;
          indicator.screenOffsetY_e0[i] = this.screenOffset_800cb568.y;
          break;
        }
      }

      s0++;
    }

    //LAB_800de644
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Something to do with forced movement. Used when Dart is halfway through his jump animation in volcano.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "Possibly movement destination X")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "Possibly movement destination Y")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "Possibly movement destination Z")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "i_144", description = "Possibly movement frames")
  @Method(0x800de668L)
  private FlowControl FUN_800de668(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;
    sobj.movementDestination_138.set(script.params_20[1].get(), script.params_20[2].get(), script.params_20[3].get());
    sobj.movementTicks_144 = script.params_20[4].get() * (3 - vsyncMode_8007a3b8);

    sobj.us_170 = 1;

    if(sobj.movementTicks_144 != 0) {
      // GH#777: These are load-bearing casts
      sobj.movementStep_148.set(
        (float)(int)(sobj.movementDestination_138.x - model.coord2_14.coord.transfer.x) / sobj.movementTicks_144,
        (float)(int)(sobj.movementDestination_138.y - model.coord2_14.coord.transfer.y) / sobj.movementTicks_144,
        (float)(int)(sobj.movementDestination_138.z - model.coord2_14.coord.transfer.z) / sobj.movementTicks_144
      );
    } else {
      sobj.movementStep_148.zero();
    }

    //LAB_800de7a8
    //LAB_800de8e8
    this.sobjs_800c6880[sobj.sobjIndex_130].setTempTicker(this::FUN_800e1f90);

    sobj.flags_190 &= 0x7fff_ffff;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Something to do with forced movement")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "Movement destination X")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "Movement destination Y")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "Movement destination Z")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "ticks", description = "Movement ticks")
  @Method(0x800de944L)
  private FlowControl FUN_800de944(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;

    sobj.movementDestination_138.set(script.params_20[1].get(), script.params_20[2].get(), script.params_20[3].get());
    sobj.movementTicks_144 = script.params_20[4].get() * (3 - vsyncMode_8007a3b8);

    sobj.movementStep_148.x = (sobj.movementDestination_138.x - model.coord2_14.coord.transfer.x) / sobj.movementTicks_144;
    sobj.movementStep_148.z = (sobj.movementDestination_138.z - model.coord2_14.coord.transfer.z) / sobj.movementTicks_144;

    //LAB_800dea34
    sobj.movementStepY_134 = ((sobj.movementDestination_138.y - model.coord2_14.coord.transfer.y) * 2 - sobj.movementTicks_144 * 7 * (sobj.movementTicks_144 - 1)) / (sobj.movementTicks_144 * 2);
    sobj.us_170 = 2;
    sobj.s_172 = 1;
    sobj.movementStepAccelerationY_18c = 7 / (2.0f / vsyncMode_8007a3b8);
    this.sobjs_800c6880[sobj.sobjIndex_130].setTempTicker(this::tickSobjMovement);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Something to do with forced movement")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "Movement destination X")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "Movement destination Y")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "Movement destination Z")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "ticks", description = "Movement ticks")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "stepAccelerationY", description = "Y step acceleration")
  @Method(0x800deba0L)
  private FlowControl FUN_800deba0(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.movementDestination_138.set(script.params_20[1].get(), script.params_20[2].get(), script.params_20[3].get());
    sobj.movementTicks_144 = script.params_20[4].get() * (3 - vsyncMode_8007a3b8);
    sobj.movementStepAccelerationY_18c = (script.params_20[5].get() + 5) / (4.0f / (vsyncMode_8007a3b8 * vsyncMode_8007a3b8));
    sobj.movementStep_148.x = (sobj.movementDestination_138.x - sobj.model_00.coord2_14.coord.transfer.x) / sobj.movementTicks_144;
    sobj.movementStep_148.z = (sobj.movementDestination_138.z - sobj.model_00.coord2_14.coord.transfer.z) / sobj.movementTicks_144;

    //LAB_800decbc
    sobj.s_174 = sobj.s_172;
    sobj.s_172 = 1;
    sobj.us_170 = 2;

    final float stepY = (sobj.movementDestination_138.y - sobj.model_00.coord2_14.coord.transfer.y) / sobj.movementTicks_144;
    sobj.movementStepY_134 = stepY - sobj.movementStepAccelerationY_18c / 2 * (sobj.movementTicks_144 - 1);

    this.sobjs_800c6880[sobj.sobjIndex_130].setTempTicker(this::tickSobjMovement);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Checks player collision")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "deltaX", description = "Movement along the X axis")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "deltaY", description = "Movement along the Y axis")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "deltaZ", description = "Movement along the Z axis")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "collidee", description = "The SubmapObject210 script index collided with, or -1 if not collided")
  @Method(0x800dee28L)
  private FlowControl scriptCheckPlayerCollision(final RunningScript<SubmapObject210> script) {
    final Vector3f deltaMovement = new Vector3f();
    final Vector3f movement = new Vector3f();

    final SubmapObject210 sobj = script.scriptState_04.innerStruct_00;
    final Model124 model = sobj.model_00;

    final short deltaX = (short)script.params_20[0].get();
    final short deltaY = (short)script.params_20[1].get();
    final short deltaZ = (short)script.params_20[2].get();

    final float angle;
    if(deltaX != 0 || deltaY != 0 || deltaZ != 0) {
      //LAB_800dee98
      //LAB_800dee9c
      //LAB_800deea0
      deltaMovement.set(deltaX, deltaY, deltaZ);
      GTE.setTransforms(worldToScreenMatrix_800c3548);
      this.transformToWorldspace(movement, deltaMovement);

      this.collisionGeometry_800cbe08.handleCollisionAndMovement(sobj.sobjIndex_12e != 0, model.coord2_14.coord.transfer, movement);

      //LAB_800def08
      angle = MathHelper.positiveAtan2(movement.z, movement.x);
    } else {
      movement.set(0.0f, model.coord2_14.coord.transfer.y, 0.0f);
      angle = model.coord2_14.transforms.rotate.y;
    }

    //LAB_800def28
    this.caches_800c68e8.playerMovement_0c.set(movement).add(model.coord2_14.coord.transfer);
    final int reachX = Math.round(sin(angle) * -sobj.playerCollisionReach_1c0);
    final int reachZ = Math.round(cos(angle) * -sobj.playerCollisionReach_1c0);
    final float colliderMinY = movement.y - sobj.playerCollisionSizeVertical_1bc;
    final float colliderMaxY = movement.y + sobj.playerCollisionSizeVertical_1bc;

    //LAB_800df008
    //LAB_800df00c
    //LAB_800df02c
    // Handle collision with other sobjs
    for(int i = 0; i < this.sobjCount_800c6730; i++) {
      final SubmapObject210 struct = this.sobjs_800c6880[i].innerStruct_00;

      if(struct != sobj && (struct.flags_190 & 0x10_0000) != 0) {
        final float x = struct.model_00.coord2_14.coord.transfer.x - (model.coord2_14.coord.transfer.x + movement.x + reachX);
        final float z = struct.model_00.coord2_14.coord.transfer.z - (model.coord2_14.coord.transfer.z + movement.z + reachZ);
        final int size = sobj.playerCollisionSizeHorizontal_1b8 + struct.playerCollisionSizeHorizontal_1b8;
        final float collideeMinY = struct.model_00.coord2_14.coord.transfer.y - struct.playerCollisionSizeVertical_1bc;
        final float collideeMaxY = struct.model_00.coord2_14.coord.transfer.y + struct.playerCollisionSizeVertical_1bc;

        //LAB_800df104
        if(size * size >= x * x + z * z && (collideeMinY >= colliderMinY && collideeMinY <= colliderMaxY || collideeMaxY >= colliderMinY && collideeMaxY <= colliderMaxY)) {
          //LAB_800df118
          script.params_20[3].set(i);
          return FlowControl.CONTINUE;
        }
      }

      //LAB_800df128
    }

    //LAB_800df13c
    script.params_20[3].set(-1);

    //LAB_800df14c
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Load a model/animation into this submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "objectIndex", description = "The map object index (0-n)")
  @Method(0x800df168L)
  private FlowControl scriptSelfLoadSobjModelAndAnimation(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptLoadSobjModelAndAnimation(script);
  }

  @ScriptDescription("Load a new animation into this submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "animIndex", description = "The anim index")
  @Method(0x800df198L)
  private FlowControl scriptSelfLoadSobjAnimation(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptLoadSobjAnimation(script);
  }

  @ScriptDescription("Get a submap object's animation")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "animIndex", description = "The anim index")
  @Method(0x800df1c8L)
  private FlowControl scriptSelfGetSobjAnimation(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptGetSobjAnimation(script);
  }

  @ScriptDescription("Set us_12a on this submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "value", description = "The new value")
  @Method(0x800df1f8L)
  private FlowControl FUN_800df1f8(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptToggleAnimationDisabled(script);
  }

  @ScriptDescription("Get us_12a from this submap object")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "value", description = "The value")
  @Method(0x800df228L)
  private FlowControl FUN_800df228(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptIsAnimationFinished(script);
  }

  @ScriptDescription("Set a submap object's position")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "The new X coordinate")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "The new Y coordinate")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "The new Z coordinate")
  @Method(0x800df258L)
  private FlowControl scriptSetModelPosition(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;
    model.coord2_14.coord.transfer.x = script.params_20[1].get();
    model.coord2_14.coord.transfer.y = script.params_20[2].get();
    model.coord2_14.coord.transfer.z = script.params_20[3].get();
    sobj.us_170 = 0;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Get a submap object's position")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "x", description = "The X coordinate")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "y", description = "The Y coordinate")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "z", description = "The Z coordinate")
  @Method(0x800df2b8L)
  private FlowControl scriptReadModelPosition(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;
    script.params_20[1].set(Math.round(model.coord2_14.coord.transfer.x));
    script.params_20[2].set(Math.round(model.coord2_14.coord.transfer.y));
    script.params_20[3].set(Math.round(model.coord2_14.coord.transfer.z));
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Set a submap object's rotation")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "The new X rotation (PSX degrees)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "The new Y rotation (PSX degrees)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "The new Z rotation (PSX degrees)")
  @Method(0x800df314L)
  private FlowControl scriptSetModelRotate(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;
    model.coord2_14.transforms.rotate.set(MathHelper.psxDegToRad(script.params_20[1].get()), MathHelper.psxDegToRad(script.params_20[2].get()), MathHelper.psxDegToRad(script.params_20[3].get()));
    sobj.rotationFrames_188 = 0;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Get a submap object's rotation")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "x", description = "The X rotation (PSX degrees)")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "y", description = "The Y rotation (PSX degrees)")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "z", description = "The Z rotation (PSX degrees)")
  @Method(0x800df374L)
  private FlowControl scriptReadModelRotate(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;
    script.params_20[1].set(MathHelper.radToPsxDeg(model.coord2_14.transforms.rotate.x));
    script.params_20[2].set(MathHelper.radToPsxDeg(model.coord2_14.transforms.rotate.y));
    script.params_20[3].set(MathHelper.radToPsxDeg(model.coord2_14.transforms.rotate.z));
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Rotates this submap object to face a point in 3D space")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "The X position to face")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "The Y position to face")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "The Z position to face")
  @Method(0x800df3d0L)
  private FlowControl scriptSelfFacePoint(final RunningScript<?> script) {
    script.params_20[3] = script.params_20[2];
    script.params_20[2] = script.params_20[1];
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptFacePoint(script);
  }

  @ScriptDescription("Set us_128 on a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "value", description = "The new value")
  @Method(0x800df410L)
  private FlowControl FUN_800df410(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptSetSobjHidden(script);
  }

  @ScriptDescription("Something to do with forced movement. Used when Dart is halfway through his jump animation in volcano.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "Use unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "Use unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "Use unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "i_144", description = "Use unknown")
  @Method(0x800df440L)
  private FlowControl FUN_800df440(final RunningScript<?> script) {
    script.params_20[4] = script.params_20[3];
    script.params_20[3] = script.params_20[2];
    script.params_20[2] = script.params_20[1];
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.FUN_800de668(script);
  }

  @ScriptDescription("Something to do with forced movement. Used when Dart is halfway through his jump animation in volcano.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "Use unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "Use unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "Use unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "i_144", description = "Use unknown")
  @Method(0x800df488L)
  private FlowControl FUN_800df488(final RunningScript<?> script) {
    script.params_20[4] = script.params_20[3];
    script.params_20[3] = script.params_20[2];
    script.params_20[2] = script.params_20[1];
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.FUN_800de944(script);
  }

  @ScriptDescription("Unknown")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "value", description = "Return value")
  @Method(0x800df4d0L)
  private FlowControl FUN_800df4d0(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.FUN_800e00cc(script);
  }

  @ScriptDescription("Unknown")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "value", description = "Return value")
  @Method(0x800df500L)
  private FlowControl FUN_800df500(final RunningScript<?> script) {
    throw new RuntimeException("Not implemented");
  }

  @ScriptDescription("Sets submap object s_172")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "value", description = "The new value")
  @Method(0x800df530L)
  private FlowControl FUN_800df530(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.FUN_800e0184(script);
  }

  @ScriptDescription("Something related to submap object animated textures")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "animatedTextureIndex", description = "The animated texture index")
  @Method(0x800df560L)
  private FlowControl FUN_800df560(final RunningScript<?> script) {
    throw new RuntimeException("Not implemented");
  }

  @ScriptDescription("Unknown")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "value", description = "Unknown")
  @Method(0x800df590L)
  private FlowControl FUN_800df590(final RunningScript<?> script) {
    throw new RuntimeException("Not implemented");
  }

  @ScriptDescription("Enable this submap object animated texture")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "animatedTextureIndex", description = "The animated texture index")
  @Method(0x800df5c0L)
  private FlowControl scriptSelfEnableTextureAnimation(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptEnableTextureAnimation(script);
  }

  @ScriptDescription("Disable this submap object animated texture")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "animatedTextureIndex", description = "The animated texture index")
  @Method(0x800df5f0L)
  private FlowControl scriptSelfDisableTextureAnimation(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptDisableTextureAnimation(script);
  }

  @ScriptDescription("Get a submap object's us_170")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "value", description = "The return value")
  @Method(0x800df620L)
  private FlowControl FUN_800df620(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.FUN_800e02c0(script);
  }

  @ScriptDescription("Attaches the camera to this submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.BOOL, name = "attach", description = "True to attach, false to detach")
  @Method(0x800df650L)
  private FlowControl scriptSelfAttachCameraToSobj(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptAttachCameraToSobj(script);
  }

  @ScriptDescription("Checks to see if the camera is attached to this submap object")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.BOOL, name = "attached", description = "True if attached, false otherwise")
  @Method(0x800df680L)
  private FlowControl scriptSelfIsCameraAttached(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)script.scriptState_04.innerStruct_00;
    script.params_20[0].set(sobj.cameraAttached_178 ? 1 : 0);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Sets the camera position")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "The new camera X position")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "The new camera Y position")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "The new camera Z position")
  @Method(0x800df6a4L)
  private FlowControl scriptSetCameraPos(final RunningScript<?> script) {
    GTE.setTransforms(worldToScreenMatrix_800c3548);
    this.setCameraPos(3 - vsyncMode_8007a3b8, new Vector3f().set(script.params_20[0].get(), script.params_20[1].get(), script.params_20[2].get())); // Retail bugfix - these were all 0

    //LAB_800df744
    for(int i = 0; i < this.sobjCount_800c6730; i++) {
      final SubmapObject210 sobj = this.sobjs_800c6880[i].innerStruct_00;
      sobj.cameraAttached_178 = false;
    }

    //LAB_800df774
    return FlowControl.CONTINUE;
  }

  /**
   * The (x, y, z) value is the full amount to rotate, i.e. it rotates by `(x, y, z) / frames` units per frame
   *
   * Used for the little mouse thing running around in the Limestone Cave
   */
  @ScriptDescription("Rotates a submap object by a relative amount over time")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "The relative X rotation (PSX degrees)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "The relative Y rotation (PSX degrees)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "The relative Z rotation (PSX degrees)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "frames", description = "The number of frames before the rotation completes")
  @Method(0x800df788L)
  private FlowControl scriptRotateSobj(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;

    final int frames = script.params_20[4].get() * (3 - vsyncMode_8007a3b8);
    sobj.rotationFrames_188 = frames;

    // Added this to fix a /0 error in the retail code
    if(frames == 0) {
      sobj.rotationAmount_17c.zero();
      return FlowControl.CONTINUE;
    }

    sobj.rotationAmount_17c.set(
      MathHelper.psxDegToRad(script.params_20[1].get()) / frames,
      MathHelper.psxDegToRad(script.params_20[2].get()) / frames,
      MathHelper.psxDegToRad(script.params_20[3].get()) / frames
    );

    //LAB_800df888
    return FlowControl.CONTINUE;
  }

  /**
   * The (x, y, z) value is the amount to rotate per frame
   */
  @ScriptDescription("Rotates a submap object by an absolute rotation over time")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "The absolute X rotation per frame (PSX degrees)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "The absolute Y rotation per frame (PSX degrees)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "The absolute Z rotation per frame (PSX degrees)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "frames", description = "The number of frames before the rotation completes")
  @Method(0x800df890L)
  private FlowControl scriptRotateSobjAbsolute(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.rotationAmount_17c.set(
      MathHelper.psxDegToRad(script.params_20[1].get()) / (2.0f / vsyncMode_8007a3b8),
      MathHelper.psxDegToRad(script.params_20[2].get()) / (2.0f / vsyncMode_8007a3b8),
      MathHelper.psxDegToRad(script.params_20[3].get()) / (2.0f / vsyncMode_8007a3b8)
    );
    sobj.rotationFrames_188 = script.params_20[4].get() * (3 - vsyncMode_8007a3b8);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Something to do with forced movement")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "Use unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "Use unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "Use unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "i_144", description = "Use unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "ui_18c", description = "Use unknown")
  @Method(0x800df904L)
  private FlowControl FUN_800df904(final RunningScript<?> script) {
    script.params_20[5] = script.params_20[4];
    script.params_20[4] = script.params_20[3];
    script.params_20[3] = script.params_20[2];
    script.params_20[2] = script.params_20[1];
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.FUN_800deba0(script);
  }

  @ScriptDescription("Rotates this submap object to face the player")
  @Method(0x800df954L)
  private FlowControl scriptFacePlayer(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)script.scriptState_04.innerStruct_00;
    sobj.model_00.coord2_14.transforms.rotate.y = MathHelper.positiveAtan2(this.caches_800c68e8.playerPos_00.z, this.caches_800c68e8.playerPos_00.x);
    sobj.rotationFrames_188 = 0;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Unknown, may get submap object's position in screen space")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "x0", description = "Screen X (head?)")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "y0", description = "Screen Y (head?)")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "x1", description = "Screen X (feet?)")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "y1", description = "Screen Y (feet?)")
  @Method(0x800df9a8L)
  private FlowControl FUN_800df9a8(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;

    final MV ls = new MV();
    GsGetLs(sobj.model_00.coord2_14, ls);

    GTE.setTransforms(ls);
    GTE.perspectiveTransform(0, 0, 0);
    script.params_20[1].set(Math.round(GTE.getScreenX(2) + 192));
    script.params_20[2].set(Math.round(GTE.getScreenY(2) + 128));

    GTE.perspectiveTransform(0, -130, 0);
    script.params_20[3].set(Math.round(GTE.getScreenX(2) + 192));
    script.params_20[4].set(Math.round(GTE.getScreenY(2) + 128));
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Gets the ID of the current submap")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "submapId", description = "The current submap id")
  @Method(0x800dfb28L)
  private FlowControl scriptGetCurrentSubmapId(final RunningScript<?> script) {
    script.params_20[0].set(submapId_800bd808);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Get the number of model parts in this submap object model")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "numberOfParts", description = "The number of model parts")
  @Method(0x800dfb44L)
  private FlowControl scriptSelfGetSobjNobj(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptGetSobjNobj(script);
  }

  @ScriptDescription("Hide a model part in this submap object model")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "partIndex", description = "The model part index")
  @Method(0x800dfb74L)
  private FlowControl scriptSelfHideModelPart(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptHideModelPart(script);
  }

  @ScriptDescription("Show a model part in this submap object model")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "partIndex", description = "The model part index")
  @Method(0x800dfba4L)
  private FlowControl scriptSelfShowModelPart(final RunningScript<?> script) {
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptShowModelPart(script);
  }

  @ScriptDescription("Make this submap object face the camera")
  @Method(0x800dfbd4L)
  private FlowControl scriptSelfFaceCamera(final RunningScript<?> script) {
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptFaceCamera(script);
  }

  @ScriptDescription("Scale a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "The new X scale (12-bit fixed-point)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "The new Y scale (12-bit fixed-point)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "The new Z scale (12-bit fixed-point)")
  @Method(0x800dfc00L)
  private FlowControl scriptScaleXyz(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;

    model.coord2_14.transforms.scale.set(
      script.params_20[1].get() / (float)0x1000,
      script.params_20[2].get() / (float)0x1000,
      script.params_20[3].get() / (float)0x1000
    );

    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Scale a submap object uniformly")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scale", description = "The new XYZ scale (12-bit fixed-point)")
  @Method(0x800dfc60L)
  private FlowControl scriptScaleUniform(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;

    model.coord2_14.transforms.scale.x = script.params_20[1].get() / (float)0x1000;
    model.coord2_14.transforms.scale.y = script.params_20[1].get() / (float)0x1000;
    model.coord2_14.transforms.scale.z = script.params_20[1].get() / (float)0x1000;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Set a submap object's Z offset")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "offset", description = "The new Z offset")
  @Method(0x800dfca0L)
  private FlowControl scriptSetModelZOffset(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.model_00.zOffset_a0 = script.params_20[1].get();
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Set this submap object's flag")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "bitIndex", description = "The flag to set")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.BOOL, name = "value", description = "The flag value")
  @Method(0x800dfcd8L)
  private FlowControl scriptSelfSetSobjFlag(final RunningScript<?> script) {
    script.params_20[2] = script.params_20[1];
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptSetSobjFlag(script);
  }

  @ScriptDescription("Get this submap object's flag")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "bitIndex", description = "The flag to get")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.BOOL, name = "value", description = "The flag value")
  @Method(0x800dfd10L)
  private FlowControl FUN_800dfd10(final RunningScript<?> script) {
    script.params_20[2] = script.params_20[1];
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptGetSobjFlag(script);
  }

  @ScriptDescription("Unknown, related to triangle indicators")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "p0")
  @Method(0x800dfd48L)
  private FlowControl FUN_800dfd48(final RunningScript<?> script) {
    throw new RuntimeException("Not implemented");
  }

  @ScriptDescription("Show the triangle indicator for this submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "The Y offset for the indicator")
  @Method(0x800dfd8cL)
  private FlowControl scriptShowAlertIndicator(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.showAlertIndicator_194 = true;
    sobj.alertIndicatorOffsetY_198 = script.params_20[1].get();
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Hide the triangle indicator for this submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @Method(0x800dfdd8L)
  private FlowControl scriptHideAlertIndicator(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.showAlertIndicator_194 = false;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Load a model/animation into a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "objectIndex", description = "The map object index (0-n)")
  @Method(0x800dfe0cL)
  private FlowControl scriptLoadSobjModelAndAnimation(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;
    model.deleteModelParts(); // Clear old model objs first

    final int index = script.params_20[1].get();

    sobj.sobjIndex_12e = index;
    model.uvAdjustments_9d = this.submap.uvAdjustments.get(index);

    this.loadModelAndAnimation(model, this.submap.objects.get(index).model, this.submap.objects.get(index).animations.get(0));

    for(final ModelPart10 part : model.modelParts_00) {
      part.obj = TmdObjLoader.fromObjTable("SobjModel (index " + index + ')', part.tmd_08);
    }

    sobj.animationFinished_12c = false;
    sobj.rotationFrames_188 = 0;

    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Load a new animation into a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "animIndex", description = "The anim index")
  @Method(0x800dfec8L)
  private FlowControl scriptLoadSobjAnimation(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;

    sobj.animIndex_132 = script.params_20[1].get();
    model.disableInterpolation_a2 = false;
    model.ub_a3 = 0;

    loadModelStandardAnimation(model, this.submap.objects.get(sobj.sobjIndex_12e).animations.get(sobj.animIndex_132));

    sobj.animationFinished_12c = false;
    sobj.flags_190 &= 0x9fff_ffff;

    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Get a submap object's animation")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "animIndex", description = "The anim index")
  @Method(0x800dff68L)
  private FlowControl scriptGetSobjAnimation(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    script.params_20[1].set(sobj.animIndex_132);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Set us_12a on a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.BOOL, name = "disabled", description = "Whether or not the animation is disabled")
  @Method(0x800dffa4L)
  private FlowControl scriptToggleAnimationDisabled(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.disableAnimation_12a = script.params_20[1].get() != 0;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Get us_12a from a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.BOOL, name = "finished", description = "Whether or not the animation is finished")
  @Method(0x800dffdcL)
  private FlowControl scriptIsAnimationFinished(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    script.params_20[1].set(sobj.animationFinished_12c ? 1 : 0);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Rotates a submap object to face a point in 3D space")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "The X position to face")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "The Y position to face (unused)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "The Z position to face")
  @Method(0x800e0018L)
  private FlowControl scriptFacePoint(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;

    final float deltaX = script.params_20[1].get() - model.coord2_14.coord.transfer.x;
    final float deltaZ = script.params_20[3].get() - model.coord2_14.coord.transfer.z;

    if(deltaX != 0.0f || deltaZ != 0.0f) {
      model.coord2_14.transforms.rotate.y = MathHelper.positiveAtan2(deltaZ, deltaX);
    }

    sobj.rotationFrames_188 = 0;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Set whether or not a submap object is hidden")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.BOOL, name = "hidden", description = "True to hide, false otherwise")
  @Method(0x800e0094L)
  private FlowControl scriptSetSobjHidden(final RunningScript<?> a0) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[a0.params_20[0].get()].innerStruct_00;
    sobj.hidden_128 = a0.params_20[1].get() != 0;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "value", description = "Return value")
  @Method(0x800e00ccL)
  private FlowControl FUN_800e00cc(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;
    final int collisionPrimitiveIndex = this.collisionGeometry_800cbe08.getCollisionPrimitiveAtPoint(model.coord2_14.coord.transfer.x, model.coord2_14.coord.transfer.y, model.coord2_14.coord.transfer.z, false);
    script.params_20[1].set(collisionPrimitiveIndex);
    sobj.collidedPrimitiveIndex_16c = collisionPrimitiveIndex;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Returns submap object s_172")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "value", description = "Return value")
  @Method(0x800e0148L)
  private FlowControl FUN_800e0148(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    script.params_20[1].set(sobj.s_172);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Sets submap object s_172")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "value", description = "The new value")
  @Method(0x800e0184L)
  private FlowControl FUN_800e0184(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.s_172 = script.params_20[1].get();
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Something related to texture animation")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "animatedTextureIndex", description = "The animated texture index")
  @Method(0x800e01bcL)
  private FlowControl FUN_800e01bc(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    this.FUN_800de138(sobj.model_00, script.params_20[1].get());
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Something related to texture animation")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "animatedTextureIndex", description = "The animated texture index")
  @Method(0x800e0204L)
  private FlowControl FUN_800e0204(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.model_00.smallerStructPtr_a4.uba_04[script.params_20[1].get()] = false;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Enable a submap object animated texture")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "animatedTextureIndex", description = "The animated texture index")
  @Method(0x800e0244L)
  private FlowControl scriptEnableTextureAnimation(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.model_00.animateTextures_ec[script.params_20[1].get()] = true;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Disable a submap object animated texture")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "animatedTextureIndex", description = "The animated texture index")
  @Method(0x800e0284L)
  private FlowControl scriptDisableTextureAnimation(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.model_00.animateTextures_ec[script.params_20[1].get()] = false;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Get a submap object's us_170")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "value", description = "The return value")
  @Method(0x800e02c0L)
  private FlowControl FUN_800e02c0(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    script.params_20[1].set(sobj.us_170);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Attaches the camera to a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.BOOL, name = "attach", description = "True to attach, false to detach")
  @Method(0x800e02fcL)
  private FlowControl scriptAttachCameraToSobj(final RunningScript<?> script) {
    final SubmapObject210 struct1 = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;

    struct1.cameraAttached_178 = script.params_20[1].get() != 0;

    if(script.params_20[1].get() != 0) {
      //LAB_800e035c
      for(int i = 0; i < this.sobjCount_800c6730; i++) {
        final SubmapObject210 struct2 = this.sobjs_800c6880[i].innerStruct_00;

        if(struct2.sobjIndex_130 != struct1.sobjIndex_130) {
          struct2.cameraAttached_178 = false;
        }

        //LAB_800e0390
      }
    }

    //LAB_800e03a0
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Get the number of model parts in a submap object model")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "numberOfParts", description = "The number of model parts")
  @Method(0x800e03a8L)
  private FlowControl scriptGetSobjNobj(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    script.params_20[1].set(sobj.model_00.modelParts_00.length);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Hide a model part in a submap object model")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "partIndex", description = "The model part index")
  @Method(0x800e03e4L)
  private FlowControl scriptHideModelPart(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;

    final int shift = script.params_20[1].get();

    //LAB_800e0430
    model.partInvisible_f4 |= 0x1L << shift;

    //LAB_800e0440
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Show a model part in a submap object model")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "scriptIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "partIndex", description = "The model part index")
  @Method(0x800e0448L)
  private FlowControl scriptShowModelPart(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;

    final int shift = script.params_20[1].get();

    //LAB_800e0498
    model.partInvisible_f4 &= ~(0x1L << shift);

    //LAB_800e04ac
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Make a submap object face the camera")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @Method(0x800e04b4L)
  private FlowControl scriptFaceCamera(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.model_00.coord2_14.transforms.rotate.y = MathHelper.positiveAtan2(this.cameraPos_800c6aa0.z, this.cameraPos_800c6aa0.x);
    sobj.rotationFrames_188 = 0;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Set a submap object's flag")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "bitIndex", description = "The flag to set")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.BOOL, name = "value", description = "The flag value")
  @Method(0x800e0520L)
  private FlowControl scriptSetSobjFlag(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;

    sobj.flags_190 = sobj.flags_190
      & ~(0x1 << script.params_20[1].get())
      | (script.params_20[2].get() & 0x1) << script.params_20[1].get();

    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Get a submap object's flag")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "bitIndex", description = "The flag to get")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.BOOL, name = "value", description = "The flag value")
  @Method(0x800e057cL)
  private FlowControl scriptGetSobjFlag(final RunningScript<?> script) {
    throw new RuntimeException("Not implemented");
  }

  @ScriptDescription("Unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "value")
  @Method(0x800e05c8L)
  private FlowControl FUN_800e05c8(final RunningScript<?> script) {
    throw new RuntimeException("Not implemented");
  }

  @ScriptDescription("Unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "index")
  @Method(0x800e05f0L)
  private FlowControl FUN_800e05f0(final RunningScript<?> script) {
    throw new RuntimeException("Not implemented");
  }

  @ScriptDescription("Enable flag lighting for a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "r", description = "The red colour")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "g", description = "The green colour")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "b", description = "The blue colour")
  @Method(0x800e0614L)
  private FlowControl scriptEnableSobjFlatLight(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.flatLightingEnabled_1c4 = true;
    sobj.flatLightRed_1c5 = script.params_20[1].get() & 0xff;
    sobj.flatLightGreen_1c6 = script.params_20[2].get() & 0xff;
    sobj.flatLightBlue_1c7 = script.params_20[3].get() & 0xff;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Disable flag lighting for a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @Method(0x800e0684L)
  private FlowControl scriptDisableSobjFlatLight(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.flatLightingEnabled_1c4 = false;
    sobj.flatLightRed_1c5 = 0x80;
    sobj.flatLightGreen_1c6 = 0x80;
    sobj.flatLightBlue_1c7 = 0x80;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Loads an interpolated animation into a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "animIndex", description = "The animation index")
  @Method(0x800e074cL)
  private FlowControl loadInterpolatedSobjAnimation(final RunningScript<?> script) {
    final SubmapObject210 sobj = SCRIPTS.getObject(script.params_20[0].get(), SubmapObject210.class);
    final Model124 model = sobj.model_00;

    sobj.animIndex_132 = script.params_20[1].get();
    model.ub_a3 = 1;
    model.disableInterpolation_a2 = false;
    loadModelStandardAnimation(model, this.submap.objects.get(sobj.sobjIndex_12e).animations.get(sobj.animIndex_132));
    sobj.animationFinished_12c = false;
    sobj.flags_190 &= 0x9fff_ffff;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Loads an uninterpolated animation into a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "animIndex", description = "The animation index")
  @Method(0x800e07f0L)
  private FlowControl loadUninterpolatedSobjAnimation(final RunningScript<?> script) {
    final SubmapObject210 sobj = SCRIPTS.getObject(script.params_20[0].get(), SubmapObject210.class);
    final Model124 model = sobj.model_00;

    sobj.animIndex_132 = script.params_20[1].get();
    model.ub_a3 = 0;
    model.disableInterpolation_a2 = true;
    loadModelStandardAnimation(model, this.submap.objects.get(sobj.sobjIndex_12e).animations.get(sobj.animIndex_132));
    sobj.animationFinished_12c = false;
    sobj.flags_190 &= 0x9fff_ffff;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Sets collision metrics for a submap object (collision type unknown)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "horizontalSize", description = "The horizontal collision size")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "verticalSize", description = "The vertical collision size")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "reach", description = "The collision reach")
  @Method(0x800e0894L)
  private FlowControl scriptSetSobjCollision2(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.collisionSizeHorizontal_1ac = script.params_20[1].get();
    sobj.collisionSizeVertical_1b0 = script.params_20[2].get();
    sobj.collisionReach_1b4 = script.params_20[3].get();
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Checks if a submap object is collided (collision type unknown)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "collided", description = "True if the submap object is collided")
  @Method(0x800e08f4L)
  private FlowControl scriptGetSobjCollidedWith2(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    script.params_20[1].set(sobj.collidedWithSobjIndex_1a8);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Sets the ambient colour of a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "r", description = "The red channel")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "g", description = "The green channel")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "b", description = "The blue channel")
  @Method(0x800e0930L)
  private FlowControl scriptSetAmbientColour(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.ambientColourEnabled_1c8 = true;
    sobj.ambientColour_1ca.set(
      (script.params_20[1].get() & 0xffff) / 4096.0f,
      (script.params_20[2].get() & 0xffff) / 4096.0f,
      (script.params_20[3].get() & 0xffff) / 4096.0f
    );
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Disables the ambient colour of a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @Method(0x800e09a0L)
  private FlowControl scriptResetAmbientColour(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.ambientColour_1ca.set(0.5f, 0.5f, 0.5f);
    sobj.ambientColourEnabled_1c8 = false;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Enable shadows for a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @Method(0x800e09e0L)
  private FlowControl scriptEnableShadow(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.model_00.shadowType_cc = 1;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Disables shadows for a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @Method(0x800e0a14L)
  private FlowControl scriptDisableShadow(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.model_00.shadowType_cc = 0;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Sets the shadow size of a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "The X size of the shadow")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "The Z size of the shadow")
  @Method(0x800e0a48L)
  private FlowControl scriptSetShadowSize(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;

    model.shadowSize_10c.x = script.params_20[1].get() / (float)0x1000;
    model.shadowSize_10c.z = script.params_20[2].get() / (float)0x1000;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Sets the shadow offset of a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "The X offset of the shadow")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "The Y offset of the shadow")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "The Z offset of the shadow")
  @Method(0x800e0a94L)
  private FlowControl scriptSetShadowOffset(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    final Model124 model = sobj.model_00;

    model.shadowOffset_118.set(script.params_20[1].get(), script.params_20[2].get(), script.params_20[3].get());
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Gets the movement vector of the player submap object")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "x", description = "The X movement")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "y", description = "The Y movement")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "z", description = "The Z movement")
  @Method(0x800e0af4L)
  private FlowControl scriptGetPlayerMovement(final RunningScript<?> script) {
    script.params_20[0].set(Math.round(this.caches_800c68e8.playerMovement_0c.x));
    script.params_20[1].set(Math.round(this.caches_800c68e8.playerMovement_0c.y));
    script.params_20[2].set(Math.round(this.caches_800c68e8.playerMovement_0c.z));
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Swaps between the normal and darker shadow textures")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "type", description = "0 = normal, 1 = darker")
  @Method(0x800e0b34L)
  private FlowControl scriptSwapShadowTexture(final RunningScript<?> script) {
    if(script.params_20[0].get() == 0) {
      new Tim(Unpacker.loadFile("shadow.tim")).uploadToGpu();
    }

    //LAB_800e0b68
    if(script.params_20[0].get() == 1) {
      new Tim(Unpacker.loadFile("SUBMAP/darker_shadow.tim")).uploadToGpu();
    }

    //LAB_800e0b8c
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Sets collision metrics for a submap object (collision type unknown)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "horizontalSize", description = "The horizontal collision size")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "verticalSize", description = "The vertical collision size")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "reach", description = "The collision reach")
  @Method(0x800e0ba0L)
  private FlowControl scriptSetSobjPlayerCollisionMetrics(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.playerCollisionSizeHorizontal_1b8 = script.params_20[1].get();
    sobj.playerCollisionSizeVertical_1bc = script.params_20[2].get();
    sobj.playerCollisionReach_1c0 = script.params_20[3].get();
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Starts loading a chapter title card")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "titleNum", description = "The title card index")
  @Method(0x800e0c00L)
  private FlowControl scriptLoadChapterTitleCard(final RunningScript<?> script) {
    this.chapterTitleCardLoaded_800c68e0 = false;
    this.chapterTitleNum_800c6738 = script.params_20[0].get();
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Checks if the chapter title card has finished loading")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.BOOL, name = "loaded", description = "True if loaded, false otherwise")
  @Method(0x800e0c24L)
  private FlowControl scriptIsChapterTitleCardLoaded(final RunningScript<?> script) {
    script.params_20[0].set(this.chapterTitleCardLoaded_800c68e0 ? 1 : 0);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Sets origin XY of chapter title card and flags it to move to the rendering state")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "originX", description = "X origin position of chapter title")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "originY", description = "Y origin position of chapter title")
  @Method(0x800e0c40L)
  private FlowControl scriptSetChapterTitleCardReadyToRender(final RunningScript<?> script) {
    this.chapterTitleNum_800c6738 |= 0x80;
    this.chapterTitleAnimationComplete_800c686e = false;
    this.chapterTitleOriginX_800c687c = script.params_20[0].get();
    this.chapterTitleOriginY_800c687e = script.params_20[1].get();
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Gets completion status of chapter title card animation")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "complete", description = "0 = not complete, 1 = complete")
  @Method(0x800e0c80L)
  private FlowControl scriptGetChapterTitleCardAnimationComplete(final RunningScript<?> script) {
    script.params_20[0].set(this.chapterTitleAnimationComplete_800c686e ? 1 : 0);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Set the number of ticks to hold on the title card before fading it out")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "ticks")
  @Method(0x800e0c9cL)
  private FlowControl scriptSetTitleCardAnimationPauseTicks(final RunningScript<?> script) {
    this.chapterTitleAnimationPauseTicksRemaining_800c673c = script.params_20[0].get();
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @Method(0x800e0cb8L)
  private FlowControl FUN_800e0cb8(final RunningScript<?> script) {
    throw new RuntimeException("Not implemented");
  }

  @Method(0x800e0d18L)
  private void loadModelAndAnimation(final Model124 model, final CContainer cContainer, final TmdAnimationFile tmdAnimFile) {
    final float transferX = model.coord2_14.coord.transfer.x;
    final float transferY = model.coord2_14.coord.transfer.y;
    final float transferZ = model.coord2_14.coord.transfer.z;

    //LAB_800e0d5c
    for(int i = 0; i < 7; i++) {
      model.animateTextures_ec[i] = false;
    }

    final int count = cContainer.tmdPtr_00.tmd.header.nobj;
    model.modelParts_00 = new ModelPart10[count];

    Arrays.setAll(model.modelParts_00, i -> new ModelPart10());

    if(cContainer.ext_04 != null) {
      final SmallerStruct smallerStruct = new SmallerStruct();
      model.smallerStructPtr_a4 = smallerStruct;
      smallerStruct.tmdExt_00 = cContainer.ext_04;

      //LAB_800e0e28
      for(int i = 0; i < 4; i++) {
        smallerStruct.tmdSubExtensionArr_20[i] = smallerStruct.tmdExt_00.tmdSubExtensionArr_00[i];
        this.FUN_800de138(model, i);
      }
    } else {
      //LAB_800e0e70
      model.smallerStructPtr_a4 = null;
    }

    //LAB_800e0e74
    model.tpage_108 = (int)((cContainer.tmdPtr_00.id & 0xffff_0000L) >> 11);

    if(cContainer.ptr_08 != null) {
      model.ptr_a8 = cContainer.ptr_08;

      //LAB_800e0eac
      for(int i = 0; i < 7; i++) {
        model.ptrs_d0[i] = model.ptr_a8._00[i];
        FUN_8002246c(model, i);
      }
    } else {
      //LAB_800e0ef0
      model.ptr_a8 = null;

      //LAB_800e0f00
      for(int i = 0; i < 7; i++) {
        model.ptrs_d0[i] = null;
      }
    }

    //LAB_800e0f10
    initObjTable2(model.modelParts_00);
    GsInitCoordinate2(null, model.coord2_14);
    prepareObjTable2(model.modelParts_00, cContainer.tmdPtr_00.tmd, model.coord2_14);

    model.zOffset_a0 = 0;
    model.disableInterpolation_a2 = false;
    model.ub_a3 = 0;
    model.partInvisible_f4 = 0;

    loadModelStandardAnimation(model, tmdAnimFile);

    model.coord2_14.coord.transfer.set(transferX, transferY, transferZ);
    model.coord2_14.transforms.scale.set(1.0f, 1.0f, 1.0f);
  }

  @Method(0x800e0ff0L)
  private void submapObjectTicker(final ScriptState<SubmapObject210> state, final SubmapObject210 sobj) {
    final Model124 model = sobj.model_00;

    if(sobj.cameraAttached_178) {
      GTE.setTransforms(worldToScreenMatrix_800c3548);
      this.setCameraPos(1, model.coord2_14.coord.transfer);
    }

    if(!sobj.hidden_128) {
      if(sobj.rotationFrames_188 != 0) {
        sobj.rotationFrames_188--;
        model.coord2_14.transforms.rotate.add(sobj.rotationAmount_17c);
      }

      if(sobj.sobjIndex_12e == 0 && this.collisionGeometry_800cbe08.dartRotationWasUpdated_800d1a8c) {
        model.coord2_14.transforms.rotate.y = this.smoothDartRotation();
      }

      applyModelRotationAndScale(model);

      if(!sobj.disableAnimation_12a) {
        final int interpolationFrameCount = (2 - vsyncMode_8007a3b8) * 2 + 1;
        animateModel(model, interpolationFrameCount);

        if(sobj.animationFinished_12c && (sobj.flags_190 & 0x2000_0000) != 0) {
          sobj.animIndex_132 = 0;
          loadModelStandardAnimation(model, this.submap.objects.get(sobj.sobjIndex_12e).animations.get(sobj.animIndex_132));
          sobj.flags_190 &= 0x9fff_ffff;
        }
      }
    }

    if(model.remainingFrames_9e == 0) {
      sobj.animationFinished_12c = true;

      if((sobj.flags_190 & 0x4000_0000) != 0) {
        sobj.disableAnimation_12a = true;
      }
    } else {
      sobj.animationFinished_12c = false;
    }

    if(sobj.showAlertIndicator_194) {
      this.renderAlertIndicator(sobj.model_00, sobj.alertIndicatorOffsetY_198);
    }

    if((sobj.flags_190 & 0x800_0000) != 0) {
      this.FUN_800e4378(sobj, 0x1000_0000L);
    }

    if((sobj.flags_190 & 0x200_0000) != 0) {
      this.FUN_800e4378(sobj, 0x400_0000L);
    }

    if((sobj.flags_190 & 0x80_0000) != 0) {
      this.FUN_800e450c(sobj, 0x100_0000L);
    }

    if((sobj.flags_190 & 0x20_0000) != 0) {
      this.FUN_800e450c(sobj, 0x40_0000L);
    }

    if(enableCollisionDebug) {
      this.renderCollisionDebug(state, sobj);
    }
  }

  public static boolean enableCollisionDebug;

  private void renderCollisionDebug(final ScriptState<SubmapObject210> state, final SubmapObject210 sobj) {
    final Model124 model = sobj.model_00;

    GTE.setTransforms(worldToScreenMatrix_800c3548);

    final Vector2f v0 = new Vector2f();
    final Vector2f v1 = new Vector2f();

    if((sobj.flags_190 & 0x200_0000) != 0 || (sobj.flags_190 & 0x800_0000) != 0) {
      this.transformCollisionVertices(model, sobj.collisionSizeHorizontal_1a0, 0, v0, v1);
      this.queueCollisionRectPacket(v0, v1, 0x800000);
    }

    if((sobj.flags_190 & 0x20_0000) != 0 || (sobj.flags_190 & 0x80_0000) != 0) {
      this.transformCollisionVertices(model, sobj.collisionSizeHorizontal_1ac, sobj.collisionReach_1b4, v0, v1);
      this.queueCollisionRectPacket(v0, v1, 0x8000);
    }

    if(this.sobjs_800c6880[0] == state) {
      this.transformCollisionVertices(model, sobj.playerCollisionSizeHorizontal_1b8, sobj.playerCollisionReach_1c0, v0, v1);
      this.queueCollisionRectPacket(v0, v1, 0x80);
    }

    if(sobj.us_170 != 0) {
      this.transformVertex(v0, sobj.model_00.coord2_14.coord.transfer);
      this.transformVertex(v1, sobj.movementDestination_138);
      this.queueMovementLinePacket(v0, v1, 0x800080);
    }
  }

  private void transformCollisionVertices(final Model124 model, final int size, final int reach, final Vector2f v0, final Vector2f v1) {
    final float reachX;
    final float reachZ;
    if(reach != 0) {
      reachX = sin(model.coord2_14.transforms.rotate.y) * -reach;
      reachZ = cos(model.coord2_14.transforms.rotate.y) * -reach;
    } else {
      reachX = 0.0f;
      reachZ = 0.0f;
    }

    final Vector3f coord = new Vector3f().set(model.coord2_14.coord.transfer).add(reachX, 0.0f, reachZ);
    this.transformVertex(v0, coord.sub(size / 2.0f, 0.0f, size / 2.0f));
    this.transformVertex(v1, coord.add(size, 0.0f, size));
  }

  private void queueCollisionRectPacket(final Vector2f v0, final Vector2f v1, final int colour) {
    GPU.queueCommand(37, new GpuCommandPoly(4)
      .translucent(Translucency.B_PLUS_F)
      .rgb(colour)
      .pos(0, v0.x, v0.y)
      .pos(1, v1.x, v0.y)
      .pos(2, v0.x, v1.y)
      .pos(3, v1.x, v1.y)
    );
  }

  private void queueMovementLinePacket(final Vector2f v0, final Vector2f v1, final int colour) {
    GPU.queueCommand(37, new GpuCommandLine()
      .translucent(Translucency.B_PLUS_F)
      .rgb(colour)
      .pos(0, v0.x, v0.y)
      .pos(1, v1.x, v1.y)
    );
  }

  @Method(0x800e123cL)
  private void submapObjectRenderer(final ScriptState<SubmapObject210> state, final SubmapObject210 sobj) {
    if(!sobj.hidden_128) {
      if(sobj.flatLightingEnabled_1c4) {
        final GsF_LIGHT light = new GsF_LIGHT();

        light.direction_00.set(0.0f, 1.0f, 0.0f);
        light.r_0c = sobj.flatLightRed_1c5 / (float)0x100;
        light.g_0d = sobj.flatLightGreen_1c6 / (float)0x100;
        light.b_0e = sobj.flatLightBlue_1c7 / (float)0x100;
        GsSetFlatLight(0, light);

        light.direction_00.set(1.0f, 0.0f, 0.0f);
        light.r_0c = sobj.flatLightRed_1c5 / (float)0x100;
        light.g_0d = sobj.flatLightGreen_1c6 / (float)0x100;
        light.b_0e = sobj.flatLightBlue_1c7 / (float)0x100;
        GsSetFlatLight(1, light);

        light.direction_00.set(0.0f, 0.0f, 1.0f);
        light.r_0c = sobj.flatLightRed_1c5 / (float)0x100;
        light.g_0d = sobj.flatLightGreen_1c6 / (float)0x100;
        light.b_0e = sobj.flatLightBlue_1c7 / (float)0x100;
        GsSetFlatLight(2, light);
      }

      //LAB_800e1310
      if(sobj.ambientColourEnabled_1c8) {
        GTE.setBackgroundColour(sobj.ambientColour_1ca.x, sobj.ambientColour_1ca.y, sobj.ambientColour_1ca.z);
      }

      //LAB_800e1334
      this.renderSmapModel(sobj.model_00);

      if(sobj.flatLightingEnabled_1c4) {
        GsSetFlatLight(0, this.GsF_LIGHT_0_800c66d8);
        GsSetFlatLight(1, this.GsF_LIGHT_1_800c66e8);
        GsSetFlatLight(2, this.GsF_LIGHT_2_800c66f8);
      }

      //LAB_800e1374
      if(sobj.ambientColourEnabled_1c8) {
        GTE.setBackgroundColour(0.5f, 0.5f, 0.5f);
      }

      //LAB_800e1390
      this.tickAttachedSobjEffects(sobj.model_00, sobj.attachedEffectData_1d0);
    }

    //LAB_800e139c
  }

  @Method(0x800e13b0L)
  private void executeSubmapMediaLoadingStage(final int collisionPrimitiveIndexForInitialPlayerPosition) {
    switch(this.mediaLoadingStage_800c68e4) {
      case LOAD_SHADOW_AND_RESET_LIGHTING_0 -> {
        new Tim(Unpacker.loadFile("shadow.tim")).uploadToGpu();

        //LAB_800e1440
        this.GsF_LIGHT_0_800c66d8.direction_00.set(0.0f, 1.0f, 0.0f);
        this.GsF_LIGHT_0_800c66d8.r_0c = 0.5f;
        this.GsF_LIGHT_0_800c66d8.g_0d = 0.5f;
        this.GsF_LIGHT_0_800c66d8.b_0e = 0.5f;
        GsSetFlatLight(0, this.GsF_LIGHT_0_800c66d8);
        this.GsF_LIGHT_1_800c66e8.direction_00.set(0.0f, 1.0f, 0.0f);
        this.GsF_LIGHT_1_800c66e8.r_0c = 0.0f;
        this.GsF_LIGHT_1_800c66e8.g_0d = 0.0f;
        this.GsF_LIGHT_1_800c66e8.b_0e = 0.0f;
        GsSetFlatLight(1, this.GsF_LIGHT_1_800c66e8);
        this.GsF_LIGHT_2_800c66f8.direction_00.set(0.0f, 1.0f, 0.0f);
        this.GsF_LIGHT_2_800c66f8.r_0c = 0.0f;
        this.GsF_LIGHT_2_800c66f8.g_0d = 0.0f;
        this.GsF_LIGHT_2_800c66f8.b_0e = 0.0f;
        GsSetFlatLight(2, this.GsF_LIGHT_2_800c66f8);

        GTE.setBackgroundColour(0.5f, 0.5f, 0.5f);
        this.mediaLoadingStage_800c68e4 = SubmapMediaState.LOAD_MUSIC_1;
      }

      case LOAD_MUSIC_1 -> {
        if(collisionPrimitiveIndexForInitialPlayerPosition == -1) {
          break;
        }

        this.submap.loadMusicAndSounds();
        this.mediaLoadingStage_800c68e4 = SubmapMediaState.WAIT_FOR_MUSIC_2;
      }

      case WAIT_FOR_MUSIC_2 -> {
        if(musicLoaded_800bd782 && (getLoadedDrgnFiles() & 0x2) == 0) {
          this.mediaLoadingStage_800c68e4 = SubmapMediaState.LOAD_EFFECT_TEXTURES_3;
        }
      }

      case LOAD_EFFECT_TEXTURES_3 -> {
        this.submapEffectsState_800f9eac = 0;

        this.reloadSubmapEffects();

        if(this.submapEffectsState_800f9eac == 1) {
          this.mediaLoadingStage_800c68e4 = SubmapMediaState.LOAD_EFFECT_MODELS_4;
        }
      }

      case LOAD_EFFECT_MODELS_4 -> {
        this.reloadSubmapEffects();

        if(this.submapEffectsState_800f9eac == 2) {
          this.mediaLoadingStage_800c68e4 = SubmapMediaState.LOAD_SOBJ_ASSETS_AND_SCRIPTS_5;
        }
      }

      // Load map assets
      case LOAD_SOBJ_ASSETS_AND_SCRIPTS_5 -> {
        this.unloadSubmapParticles_800c6870 = false;
        this.submap.loadAssets(() -> this.mediaLoadingStage_800c68e4 = SubmapMediaState.FINALIZE_SUBMAP_LOADING_7);
        this.mediaLoadingStage_800c68e4 = SubmapMediaState.WAIT_FOR_SOBJ_ASSETS_AND_SCRIPTS_6;
      }

      // Load submap objects
      case FINALIZE_SUBMAP_LOADING_7 -> {
        FUN_800218f0();

        // Removed setting of unused sobjCount static
        this.sobjCount_800c6730 = this.submap.objects.size();

        this.firstMovement = true;

        //LAB_800e1914
        final ScriptState<Void> submapController = SCRIPTS.allocateScriptState(0, "Submap controller", 0, null);
        this.submapControllerState_800c6740 = submapController;
        submapController.loadScriptFile(this.submap.script);

        //LAB_800e1b20
        //LAB_800e1b54
        for(int i = 0; i < this.sobjCount_800c6730; i++) {
          final SubmapObject obj = this.submap.objects.get(i);

          final String name = "Submap object " + i + " (file " + i * 33 + ')';
          final ScriptState<SubmapObject210> state = SCRIPTS.allocateScriptState(name, new SubmapObject210(name));
          this.sobjs_800c6880[i] = state;
          state.setTicker(this::submapObjectTicker);
          state.setRenderer(this::submapObjectRenderer);
          state.setDestructor(this::scriptDestructor);
          state.loadScriptFile(obj.script);

          final Model124 model = state.innerStruct_00.model_00;
          model.uvAdjustments_9d = this.submap.uvAdjustments.get(i);

          final CContainer tmd = this.submap.objects.get(i).model;
          final TmdAnimationFile anim = obj.animations.get(0);
          initModel(model, tmd, anim);

          if(i == 0) { // Player
            this.loadModelAndAnimation(this.playerModel_800c6748, tmd, anim);
            this.playerModel_800c6748.coord2_14.coord.transfer.set(0, 0, 0);
            this.playerModel_800c6748.coord2_14.transforms.rotate.zero();
          }

          //LAB_800e1c50
          state.innerStruct_00.hidden_128 = false;
          state.innerStruct_00.disableAnimation_12a = false;
          state.innerStruct_00.animationFinished_12c = false;
          state.innerStruct_00.sobjIndex_12e = i;
          state.innerStruct_00.sobjIndex_130 = i;
          state.innerStruct_00.animIndex_132 = 0;
          state.innerStruct_00.movementStepY_134 = 0.0f;
          state.innerStruct_00.movementTicks_144 = 0;
          state.innerStruct_00.collidedPrimitiveIndex_16c = -1;
          state.innerStruct_00.us_170 = 0;
          state.innerStruct_00.s_172 = 0;
          state.innerStruct_00.rotationFrames_188 = 0;
          state.innerStruct_00.showAlertIndicator_194 = false;
          state.innerStruct_00.collidedWithSobjIndex_19c = -1;
          state.innerStruct_00.collisionSizeHorizontal_1a0 = 20;
          state.innerStruct_00.collisionSizeVertical_1a4 = 20;
          state.innerStruct_00.collidedWithSobjIndex_1a8 = -1;
          state.innerStruct_00.collisionSizeHorizontal_1ac = 20;
          state.innerStruct_00.collisionSizeVertical_1b0 = 20;
          state.innerStruct_00.collisionReach_1b4 = 50;
          state.innerStruct_00.playerCollisionSizeHorizontal_1b8 = 20;
          state.innerStruct_00.playerCollisionSizeVertical_1bc = 20;
          state.innerStruct_00.playerCollisionReach_1c0 = 50;
          state.innerStruct_00.flatLightingEnabled_1c4 = false;
          state.innerStruct_00.flatLightRed_1c5 = 0x80;
          state.innerStruct_00.flatLightGreen_1c6 = 0x80;
          state.innerStruct_00.flatLightBlue_1c7 = 0x80;

          state.innerStruct_00.cameraAttached_178 = i == 0; // Player

          //LAB_800e1ce4
          final SobjPos14 pos = sobjPositions_800bd818[i];
          model.coord2_14.coord.transfer.set(pos.pos_00);
          model.coord2_14.transforms.rotate.set(pos.rot_0c);

          state.innerStruct_00.movementStepAccelerationY_18c = 7 / (2.0f / vsyncMode_8007a3b8);
          state.innerStruct_00.flags_190 = 0;

          if(i == 0) {
            state.innerStruct_00.flags_190 = 0x1;
            this.restoreOrSetPlayerPosition(collisionPrimitiveIndexForInitialPlayerPosition, model);
          }

          //LAB_800e1d60
          this.initAttachedSobjEffectData(state.innerStruct_00.attachedEffectData_1d0);

          for(final ModelPart10 part : model.modelParts_00) {
            part.obj = TmdObjLoader.fromObjTable("SobjModel (index " + i + ')', part.tmd_08);
          }
        }

        //LAB_800e1d88
        this.chapterTitleState_800c6708 = 0;
        this.chapterTitleAnimationTicksRemaining_800c670a = 0;
        this.chapterTitleDropShadowOffsetX_800c670c = 0;
        this.chapterTitleDropShadowOffsetY_800c670e = 0;
        this.chapterTitleCardMrg_800c6710 = null;
        this.chapterTitleNumberOffsetX_800c6714 = 0;
        this.chapterTitleNumberOffsetY_800c6718 = 0;
        this.chapterTitleNameOffsetX_800c671c = 0;
        this.chapterTitleNameOffsetY_800c6720 = 0;

        this.chapterTitleNum_800c6738 = 0;
        this.chapterTitleAnimationPauseTicksRemaining_800c673c = 60;

        this.chapterTitleAnimationComplete_800c686e = false;
        this.chapterTitleOriginX_800c687c = 0;
        this.chapterTitleOriginY_800c687e = 0;
        this.chapterTitleCardLoaded_800c68e0 = false;
        this.mediaLoadingStage_800c68e4 = SubmapMediaState.DONE;

        this.triangleIndicator_800c69fc = new TriangleIndicator140();

        this.cameraPos_800c6aa0.set(rview2_800bd7e8.viewpoint_00).sub(rview2_800bd7e8.refpoint_0c);

        new Tim(Unpacker.loadFile("SUBMAP/alert.tim")).uploadToGpu();
        this.resetTriangleIndicators();

        //LAB_800e1ecc
        for(int i = 0; i < 32; i++) {
          this.indicatorTickCountArray_800c6970[i] = -1;
        }

        transitioningFromCombatToSubmap_800bd7b8 = false;
      }

      case DONE -> {
        //LAB_800e1f40
        submapFullyLoaded_800bd7b4 = true;

        if(this.chapterTitleNum_800c6738 != 0) {
          this.handleAndRenderChapterTitle();
        }
      }
    }
  }

  /** Handles cutscene movement */
  @Method(0x800e1f90L)
  private boolean FUN_800e1f90(final ScriptState<SubmapObject210> state, final SubmapObject210 sobj) {
    final Model124 model = sobj.model_00;

    if((sobj.flags_190 & 0x8000_0000) != 0) {
      return false;
    }

    //LAB_800e2014
    //LAB_800e20d8
    sobj.movementTicks_144--;

    if(sobj.s_172 == 0) {
      final Vector3f movement = new Vector3f();

      if((sobj.flags_190 & 0x1) != 0) { // Is player
        final Vector3f sp0x18 = new Vector3f();
        sp0x18.set(sobj.movementStep_148).mul(2.0f / vsyncMode_8007a3b8);
        GTE.setTransforms(worldToScreenMatrix_800c3548);
        this.transformToWorldspace(movement, sp0x18);
      } else {
        //LAB_800e2134
        movement.set(sobj.movementStep_148).mul(2.0f / vsyncMode_8007a3b8);
      }

      //LAB_800e2140
      final int collidedPrimitiveIndex = this.collisionGeometry_800cbe08.handleCollisionAndMovement(sobj.sobjIndex_12e != 0, model.coord2_14.coord.transfer, movement);
      if(collidedPrimitiveIndex >= 0 && this.isWalkable(collidedPrimitiveIndex)) {
        model.coord2_14.coord.transfer.x += movement.x / (2.0f / vsyncMode_8007a3b8);
        model.coord2_14.coord.transfer.y = movement.y;
        model.coord2_14.coord.transfer.z += movement.z / (2.0f / vsyncMode_8007a3b8);
      }

      //LAB_800e21bc
      sobj.collidedPrimitiveIndex_16c = collidedPrimitiveIndex;
    } else {
      //LAB_800e21c4
      model.coord2_14.coord.transfer.add(sobj.movementStep_148);
    }

    //LAB_800e21e8
    if(sobj.movementTicks_144 != 0) {
      //LAB_800e21f8
      return false;
    }

    //LAB_800e2200
    sobj.us_170 = 0;

    //LAB_800e2204
    return true;
  }

  @Method(0x800e2220L)
  private void unloadSmap() {
    this.collisionGeometry_800cbe08.unloadCollision();
    this.clearLatches();

    this.submapControllerState_800c6740.deallocateWithChildren();

    if(this.chapterTitleCardMrg_800c6710 != null) {
      this.chapterTitleCardMrg_800c6710 = null;
    }

    //LAB_800e226c
    submapFullyLoaded_800bd7b4 = false;

    //LAB_800e229c
    for(int i = 0; i < this.sobjCount_800c6730; i++) {
      final SobjPos14 pos = sobjPositions_800bd818[i];

      final ScriptState<SubmapObject210> sobjState = this.sobjs_800c6880[i];
      if(sobjState != null) {
        final SubmapObject210 sobj = sobjState.innerStruct_00;
        final Model124 model = sobj.model_00;
        pos.pos_00.set(model.coord2_14.coord.transfer);
        pos.rot_0c.set(model.coord2_14.transforms.rotate);
        this.sobjs_800c6880[i].deallocateWithChildren();
      } else {
        //LAB_800e231c
        pos.pos_00.zero();
        pos.rot_0c.zero();
      }
    }

    //LAB_800e2350
    _800bd7b0 = 1;

    scriptDeallocateAllTextboxes(null);

    this.unloadSubmapParticles_800c6870 = true;
    this.submap.unload();
    this.submap = null;

    this.submapEffectsState_800f9eac = -1;
    this.reloadSubmapEffects();
    this.triangleIndicator_800c69fc = null;
    new Tim(Unpacker.loadFile("shadow.tim")).uploadToGpu();
    this.mapIndicator.destroy();
  }

  @Method(0x800e2428L)
  private void cacheSobjScreenBounds(final int sobjIndex) {
    final MV ls = new MV();

    final SubmapCaches80 caches = this.caches_800c68e8;

    GsGetLs(SCRIPTS.getObject(sobjIndex, SubmapObject210.class).model_00.coord2_14, ls);
    GTE.setTransforms(ls);

    GTE.perspectiveTransform(0, 0, 0);
    caches.bottomMiddle_70.set(GTE.getScreenX(2) + 192, GTE.getScreenY(2) + 128);

    GTE.perspectiveTransform(0, -130, 0);
    caches.topMiddle_78.set(GTE.getScreenX(2) + 192, GTE.getScreenY(2) + 128);

    GTE.perspectiveTransform(-20, 0, 0);
    caches.bottomLeft_68.set(GTE.getScreenX(2) + 192, GTE.getScreenY(2) + 128);

    GTE.perspectiveTransform(20, 0, 0);
    caches.bottomRight_60.set(GTE.getScreenX(2) + 192, GTE.getScreenY(2) + 128);
  }

  @Method(0x800e2648L)
  private void handleAndRenderChapterTitle() {
    final int chapterTitleState = this.chapterTitleState_800c6708;

    if(chapterTitleState == 0) {
      //LAB_800e26c0
      final int chapterTitleNum = this.chapterTitleNum_800c6738;

      if(chapterTitleNum == 1) {
        //LAB_800e2700
        //LAB_800e2794
        loadDrgnDir(0, 6670, files -> this.submapAssetsLoadedCallback(files, 0x10));
      } else if(chapterTitleNum == 2) {
        //LAB_800e2728
        //LAB_800e2794
        loadDrgnDir(0, 6671, files -> this.submapAssetsLoadedCallback(files, 0x10));
        //LAB_800e26e8
      } else if(chapterTitleNum == 3) {
        //LAB_800e2750
        //LAB_800e2794
        loadDrgnDir(0, 6672, files -> this.submapAssetsLoadedCallback(files, 0x10));
      } else if(chapterTitleNum == 4) {
        //LAB_800e2778
        //LAB_800e2794
        loadDrgnDir(0, 6673, files -> this.submapAssetsLoadedCallback(files, 0x10));
      }

      //LAB_800e27a4
      this.chapterTitleState_800c6708++;
      return;
    }

    if(chapterTitleState == 1) {
      //LAB_800e27b8
      if(this.chapterTitleCardLoaded_800c68e0 && (this.chapterTitleNum_800c6738 & 0x80) != 0) {
        this.chapterTitleState_800c6708++;
      }

      return;
    }

    if(chapterTitleState == 2) {
      //LAB_800e27e8
      final long currentTick = this.chapterTitleAnimationTicksRemaining_800c670a;

      //LAB_800e284c
      if(currentTick == 0) {
        //LAB_800e2860
        new Tim(this.chapterTitleCardMrg_800c6710.get(5)).uploadToGpu();
        new Tim(this.chapterTitleCardMrg_800c6710.get(13)).uploadToGpu();

        //LAB_800e2980
        this.chapterTitleBrightness_800c6728 = 0;
        this.chapterTitleIsTranslucent_800c6724 = true;
        this.chapterTitleNumberOffsetX_800c6714 = 32;
        this.chapterTitleNumberOffsetY_800c6718 = 16;
        this.chapterTitleNameOffsetX_800c671c = 64;
        this.chapterTitleNameOffsetY_800c6720 = 16;
        this.chapterTitleAnimationTicksRemaining_800c670a++;
      } else if(currentTick == 34) {
        //LAB_800e30c0
        this.chapterTitleDropShadowOffsetX_800c670c++;

        if(this.chapterTitleDropShadowOffsetX_800c670c == 3) {
          this.chapterTitleDropShadowOffsetY_800c670e = 1;
          this.chapterTitleAnimationTicksRemaining_800c670a++;
        }
      } else if(currentTick == 35) {
        //LAB_800e30f8
        this.chapterTitleAnimationPauseTicksRemaining_800c673c--;

        if(this.chapterTitleAnimationPauseTicksRemaining_800c673c == 0) {
          this.chapterTitleAnimationTicksRemaining_800c670a = 201;
        }
      } else if(currentTick == 233) {
        //LAB_800e376c
        this.chapterTitleCardMrg_800c6710 = null;
        this.chapterTitleState_800c6708++;
      } else if(currentTick >= 35) {
        //LAB_800e2828
        if(currentTick < 233) {
          if(currentTick >= 201) {
            //LAB_800e311c
            if(currentTick == 212) {
              new Tim(this.chapterTitleCardMrg_800c6710.get(1)).uploadToGpu();
              new Tim(this.chapterTitleCardMrg_800c6710.get(9)).uploadToGpu();

              //LAB_800e3248
              //LAB_800e3254
            } else if(currentTick == 216) {
              new Tim(this.chapterTitleCardMrg_800c6710.get(2)).uploadToGpu();
              new Tim(this.chapterTitleCardMrg_800c6710.get(10)).uploadToGpu();

              //LAB_800e3384
              //LAB_800e3390
            } else if(currentTick == 220) {
              new Tim(this.chapterTitleCardMrg_800c6710.get(3)).uploadToGpu();
              new Tim(this.chapterTitleCardMrg_800c6710.get(11)).uploadToGpu();

              //LAB_800e34c0
              //LAB_800e34cc
            } else if(currentTick == 224) {
              new Tim(this.chapterTitleCardMrg_800c6710.get(4)).uploadToGpu();
              new Tim(this.chapterTitleCardMrg_800c6710.get(12)).uploadToGpu();

              //LAB_800e35fc
              //LAB_800e3608
            } else if(currentTick == 228) {
              new Tim(this.chapterTitleCardMrg_800c6710.get(5)).uploadToGpu();
              new Tim(this.chapterTitleCardMrg_800c6710.get(13)).uploadToGpu();
            }

            //LAB_800e3744
            this.chapterTitleIsTranslucent_800c6724 = true;
            this.chapterTitleBrightness_800c6728 -= 4;
          }

          //LAB_800e3790
          this.chapterTitleAnimationTicksRemaining_800c670a++;
        }
      } else if(currentTick >= 33) {
        //LAB_800e3070
        this.chapterTitleIsTranslucent_800c6724 = false;
        this.chapterTitleNameOffsetY_800c6720 = 0;
        this.chapterTitleNameOffsetX_800c671c = 0;
        this.chapterTitleNumberOffsetY_800c6718 = 0;
        this.chapterTitleNumberOffsetX_800c6714 = 0;
        this.chapterTitleBrightness_800c6728 = 128;
        this.chapterTitleAnimationTicksRemaining_800c670a++;
        this.chapterTitleDropShadowOffsetX_800c670c = 1;
        this.chapterTitleDropShadowOffsetY_800c670e = 0;
      } else if((int)currentTick > 0) {
        //LAB_800e29d4
        if(currentTick == 4) {
          new Tim(this.chapterTitleCardMrg_800c6710.get(4)).uploadToGpu();
          new Tim(this.chapterTitleCardMrg_800c6710.get(12)).uploadToGpu();

          //LAB_800e2afc
          //LAB_800e2b08
        } else if(currentTick == 8) {
          new Tim(this.chapterTitleCardMrg_800c6710.get(3)).uploadToGpu();
          new Tim(this.chapterTitleCardMrg_800c6710.get(11)).uploadToGpu();

          //LAB_800e2c38
          //LAB_800e2c44
        } else if(currentTick == 12) {
          new Tim(this.chapterTitleCardMrg_800c6710.get(2)).uploadToGpu();
          new Tim(this.chapterTitleCardMrg_800c6710.get(10)).uploadToGpu();

          //LAB_800e2d74
          //LAB_800e2d80
        } else if(currentTick == 16) {
          new Tim(this.chapterTitleCardMrg_800c6710.get(1)).uploadToGpu();
          new Tim(this.chapterTitleCardMrg_800c6710.get(9)).uploadToGpu();

          //LAB_800e2eb0
          //LAB_800e2ebc
        } else if(currentTick == 20) {
          new Tim(this.chapterTitleCardMrg_800c6710.get(0)).uploadToGpu();
          new Tim(this.chapterTitleCardMrg_800c6710.get(8)).uploadToGpu();

          //LAB_800e2fec
        }

        //LAB_800e2ff8
        this.chapterTitleBrightness_800c6728 += 4;
        this.chapterTitleNumberOffsetX_800c6714--;
        this.chapterTitleNameOffsetX_800c671c -= 2;

        // Decrement Y-offset every other tick
        if((this.chapterTitleAnimationTicksRemaining_800c670a & 0x1) == 0) {
          this.chapterTitleNumberOffsetY_800c6718--;
          this.chapterTitleNameOffsetY_800c6720--;
        }

        //LAB_800e3038
        //LAB_800e3064
        this.chapterTitleAnimationTicksRemaining_800c670a++;
      } else {
        //LAB_800e3790
        this.chapterTitleAnimationTicksRemaining_800c670a++;
      }

      //LAB_800e37a0
      int left = this.chapterTitleOriginX_800c687c + this.chapterTitleNumberOffsetX_800c6714 - 58;
      int top = this.chapterTitleOriginY_800c687e + this.chapterTitleNumberOffsetY_800c6718 - 66;
      int right = this.chapterTitleOriginX_800c687c - (this.chapterTitleNumberOffsetX_800c6714 - 34);
      int bottom = this.chapterTitleOriginY_800c687e - (this.chapterTitleNumberOffsetY_800c6718 + 30);

      // Chapter number text
      final GpuCommandPoly cmd1 = new GpuCommandPoly(4)
        .bpp(Bpp.BITS_4)
        .monochrome(this.chapterTitleBrightness_800c6728)
        .clut(512, 510)
        .vramPos(512, 256)
        .uv(0,  0, 64)
        .uv(1, 91, 64)
        .uv(2,  0, 99)
        .uv(3, 91, 99)
        .pos(0, left, top)
        .pos(1, right, top)
        .pos(2, left, bottom)
        .pos(3, right, bottom);

      if(this.chapterTitleIsTranslucent_800c6724) {
        cmd1.translucent(Translucency.B_PLUS_F);
      }

      GPU.queueCommand(28, cmd1);

      left = this.chapterTitleOriginX_800c687c - (this.chapterTitleNameOffsetX_800c671c + 140);
      top = this.chapterTitleOriginY_800c687e - (this.chapterTitleNameOffsetY_800c6720 + 16);
      right = this.chapterTitleOriginX_800c687c + this.chapterTitleNameOffsetX_800c671c + 116;
      bottom = this.chapterTitleOriginY_800c687e + this.chapterTitleNameOffsetY_800c6720 + 45;

      // Chapter name text
      final GpuCommandPoly cmd2 = new GpuCommandPoly(4)
        .bpp(Bpp.BITS_4)
        .monochrome(this.chapterTitleBrightness_800c6728)
        .clut(512, 508)
        .vramPos(512, 256)
        .uv(0,   0,  0)
        .uv(1, 255,  0)
        .uv(2,   0, 60)
        .uv(3, 255, 60)
        .pos(0, left, top)
        .pos(1, right, top)
        .pos(2, left, bottom)
        .pos(3, right, bottom);

      if(this.chapterTitleIsTranslucent_800c6724) {
        cmd2.translucent(Translucency.B_PLUS_F);
      }

      GPU.queueCommand(28, cmd2);

      if(this.chapterTitleDropShadowOffsetX_800c670c != 0) {
        left = this.chapterTitleDropShadowOffsetX_800c670c + this.chapterTitleOriginX_800c687c + this.chapterTitleNumberOffsetX_800c6714 - 58;
        top = this.chapterTitleDropShadowOffsetY_800c670e + this.chapterTitleOriginY_800c687e + this.chapterTitleNumberOffsetY_800c6718 - 66;
        right = this.chapterTitleDropShadowOffsetX_800c670c + this.chapterTitleOriginX_800c687c - (this.chapterTitleNumberOffsetX_800c6714 - 34);
        bottom = this.chapterTitleDropShadowOffsetY_800c670e + this.chapterTitleOriginY_800c687e - (this.chapterTitleNumberOffsetY_800c6718 + 30);

        // Chapter number drop shadow
        final GpuCommandPoly cmd3 = new GpuCommandPoly(4)
          .bpp(Bpp.BITS_4)
          .translucent(Translucency.HALF_B_PLUS_HALF_F)
          .monochrome(this.chapterTitleBrightness_800c6728)
          .clut(512, 511)
          .vramPos(512, 256)
          .uv(0,  0, 64)
          .uv(1, 91, 64)
          .uv(2,  0, 99)
          .uv(3, 91, 99)
          .pos(0, left, top)
          .pos(1, right, top)
          .pos(2, left, bottom)
          .pos(3, right, bottom);

        if((this.chapterTitleNum_800c6738 & 0xf) - 2 < 3) {
          cmd3.translucent(Translucency.B_MINUS_F);
        }

        //LAB_800e3afc
        if((this.chapterTitleNum_800c6738 & 0xf) == 1) {
          cmd3.translucent(Translucency.B_PLUS_F);
        }

        //LAB_800e3b14
        GPU.queueCommand(28, cmd3);

        left = this.chapterTitleDropShadowOffsetX_800c670c + this.chapterTitleOriginX_800c687c - (this.chapterTitleNameOffsetX_800c671c + 140);
        top = this.chapterTitleDropShadowOffsetY_800c670e + this.chapterTitleOriginY_800c687e - (this.chapterTitleNameOffsetY_800c6720 + 16);
        right = this.chapterTitleDropShadowOffsetX_800c670c + this.chapterTitleOriginX_800c687c + this.chapterTitleNameOffsetX_800c671c + 116;
        bottom = this.chapterTitleDropShadowOffsetY_800c670e + this.chapterTitleOriginY_800c687e + this.chapterTitleNameOffsetY_800c6720 + 45;

        // Chapter name drop shadow
        final GpuCommandPoly cmd4 = new GpuCommandPoly(4)
          .bpp(Bpp.BITS_4)
          .translucent(Translucency.HALF_B_PLUS_HALF_F)
          .monochrome(this.chapterTitleBrightness_800c6728)
          .clut(512, 509)
          .vramPos(512, 256)
          .uv(0,   0,  0)
          .uv(1, 255,  0)
          .uv(2,   0, 60)
          .uv(3, 255, 60)
          .pos(0, left, top)
          .pos(1, right, top)
          .pos(2, left, bottom)
          .pos(3, right, bottom);

        if((this.chapterTitleNum_800c6738 & 0xf) - 2 < 3) {
          cmd4.translucent(Translucency.B_MINUS_F);
        }

        //LAB_800e3c20
        if((this.chapterTitleNum_800c6738 & 0xf) == 1) {
          cmd4.translucent(Translucency.B_PLUS_F);
        }

        //LAB_800e3c3c
        GPU.queueCommand(28, cmd4);
      }

      return;
    }

    //LAB_800e26a4
    if(chapterTitleState == 3) {
      //LAB_800e3c60
      this.chapterTitleNum_800c6738 = 0;
      this.chapterTitleOriginY_800c687e = 0;
      this.chapterTitleOriginX_800c687c = 0;
      this.chapterTitleCardLoaded_800c68e0 = false;
      this.chapterTitleDropShadowOffsetX_800c670c = 0;
      this.chapterTitleAnimationTicksRemaining_800c670a = 0;
      this.chapterTitleState_800c6708 = 0;
      this.chapterTitleAnimationComplete_800c686e = true;
    }
  }

  @Method(0x800e3d80L)
  private void submapAssetsLoadedCallback(final List<FileData> files, final int assetType) {
    switch(assetType) {
      // Chapter title cards
      case 0x10 -> {
        this.chapterTitleCardMrg_800c6710 = files;
        this.chapterTitleCardLoaded_800c68e0 = true;
      }
    }
  }

  @Method(0x800e3df4L)
  private void scriptDestructor(final ScriptState<SubmapObject210> state, final SubmapObject210 sobj) {
    //LAB_800e3e24
    for(int i = 0; i < this.sobjCount_800c6730; i++) {
      if(this.sobjs_800c6880[i] == state) {
        this.sobjs_800c6880[i] = null;
      }

      //LAB_800e3e38
    }

    sobj.model_00.deleteModelParts();

    //LAB_800e3e48
  }

  @Method(0x800e3e60L)
  private boolean tickBasicMovement(final ScriptState<SubmapObject210> state, final SubmapObject210 sobj) {
    sobj.movementStart.lerp(sobj.movementDestination_138, (sobj.movementTicks_144 + 1.0f) / sobj.movementTicksTotal, sobj.model_00.coord2_14.coord.transfer);
    sobj.movementTicks_144++;
    return sobj.movementTicks_144 >= sobj.movementTicksTotal;
  }

  /** Used in teleporter just before Melbu */
  @Method(0x800e3e74L)
  private boolean tickSobjMovement(final ScriptState<SubmapObject210> state, final SubmapObject210 sobj) {
    final Model124 model = sobj.model_00;

    model.coord2_14.coord.transfer.y += sobj.movementStepY_134;

    //LAB_800e3ec0
    //LAB_800e3f3c
    model.coord2_14.coord.transfer.x += sobj.movementStep_148.x;
    model.coord2_14.coord.transfer.z += sobj.movementStep_148.z;
    sobj.movementStepY_134 += sobj.movementStepAccelerationY_18c;
    sobj.movementTicks_144--;
    if(sobj.movementTicks_144 != 0) {
      return false;
    }

    //LAB_800e3f7c
    sobj.us_170 = 0;
    sobj.movementStepY_134 = 0;
    model.coord2_14.coord.transfer.set(sobj.movementDestination_138);
    sobj.s_172 = sobj.s_174;
    return true;
  }

  @Override
  @Method(0x800e3facL)
  public void menuClosed() {
    if(!transitioningFromCombatToSubmap_800bd7b8) {
      this.submap.restoreAssets();
    }

    //LAB_800e4008
  }

  @Method(0x800e4018L)
  private void setIndicatorStatusAndResetIndicatorTickCountOnReenable() {
    if(gameState_800babc8.indicatorsDisabled_4e3) {
      if(!this.indicatorDisabledForCutscene_800f64ac) {
        this.indicatorDisabledForCutscene_800f64ac = true;
      }
    } else if(this.indicatorDisabledForCutscene_800f64ac) {
      this.indicatorDisabledForCutscene_800f64ac = false;
      this.indicatorTickCountArray_800c6970[31] = 0;
    }
  }

  /** sobj/sobj collision */
  @Method(0x800e4378L)
  private void FUN_800e4378(final SubmapObject210 sobj, final long a1) {
    final Model124 model = sobj.model_00;

    sobj.collidedWithSobjIndex_19c = -1;

    final float colliderMinY = model.coord2_14.coord.transfer.y - sobj.collisionSizeVertical_1a4;
    final float colliderMaxY = model.coord2_14.coord.transfer.y + sobj.collisionSizeVertical_1a4;

    //LAB_800e43b8
    //LAB_800e43ec
    //LAB_800e43f0
    //LAB_800e4414
    for(int i = 0; i < this.sobjCount_800c6730; i++) {
      final SubmapObject210 sobj2 = this.sobjs_800c6880[i].innerStruct_00;
      final Model124 model2 = sobj2.model_00;

      if(sobj2 != sobj && (sobj2.flags_190 & a1) != 0) {
        final float dx = model2.coord2_14.coord.transfer.x - model.coord2_14.coord.transfer.x;
        final float dz = model2.coord2_14.coord.transfer.z - model.coord2_14.coord.transfer.z;
        final int size = sobj.collisionSizeHorizontal_1a0 + sobj2.collisionSizeHorizontal_1a0;
        final float collideeMinY = model2.coord2_14.coord.transfer.y - sobj2.collisionSizeVertical_1a4;
        final float collideeMaxY = model2.coord2_14.coord.transfer.y + sobj2.collisionSizeVertical_1a4;

        //LAB_800e44d0
        //LAB_800e44e0
        if(size * size >= dx * dx + dz * dz && (colliderMaxY >= collideeMinY && colliderMinY <= collideeMinY || colliderMaxY >= collideeMaxY && colliderMinY <= collideeMaxY) && sobj.collidedWithSobjIndex_19c == -1) {
          sobj.collidedWithSobjIndex_19c = i;
        }
      }
    }
  }

  /** sobj/sobj collision */
  @Method(0x800e450cL)
  private void FUN_800e450c(final SubmapObject210 sobj, final long a1) {
    final Model124 model = sobj.model_00;

    sobj.collidedWithSobjIndex_1a8 = -1;

    final int reachX = Math.round(sin(model.coord2_14.transforms.rotate.y) * -sobj.collisionReach_1b4);
    final int reachZ = Math.round(cos(model.coord2_14.transforms.rotate.y) * -sobj.collisionReach_1b4);
    final float colliderMinY = model.coord2_14.coord.transfer.y - sobj.collisionSizeVertical_1b0;
    final float colliderMaxY = model.coord2_14.coord.transfer.y + sobj.collisionSizeVertical_1b0;

    //LAB_800e45d8
    //LAB_800e45dc
    //LAB_800e4600
    for(int i = 0; i < this.sobjCount_800c6730; i++) {
      final SubmapObject210 sobj2 = this.sobjs_800c6880[i].innerStruct_00;
      final Model124 model2 = sobj2.model_00;

      if(sobj2 != sobj && (sobj2.flags_190 & a1) != 0) {
        final float dx = model2.coord2_14.coord.transfer.x - (model.coord2_14.coord.transfer.x + reachX);
        final float dz = model2.coord2_14.coord.transfer.z - (model.coord2_14.coord.transfer.z + reachZ);
        final int size = sobj.collisionSizeHorizontal_1ac + sobj2.collisionSizeHorizontal_1ac;

        final float collideeMinY = model2.coord2_14.coord.transfer.y - sobj2.collisionSizeVertical_1b0;
        final float collideeMaxY = model2.coord2_14.coord.transfer.y + sobj2.collisionSizeVertical_1b0;

        //LAB_800e46bc
        //LAB_800e46cc
        if(size * size >= dx * dx + dz * dz && (collideeMinY >= colliderMinY && collideeMinY <= colliderMaxY || collideeMaxY >= colliderMinY && collideeMaxY <= colliderMaxY) && sobj.collidedWithSobjIndex_1a8 == -1) {
          sobj.collidedWithSobjIndex_1a8 = i;
        }
      }

      //LAB_800e46e0
    }

    //LAB_800e46f0
  }

  @Method(0x800e4708L)
  private void renderSubmap() {
    this.renderAttachedSobjEffects();
    this.renderSubmapOverlays();
    this.handleAndRenderSubmapEffects();
    applyModelRotationAndScale(this.playerModel_800c6748);
    this.submap.draw();
  }

  /**
   * @param parent The model that the indicator is attached to
   * @param y      The Y offset from the model
   */
  @Method(0x800e4774L)
  private void renderAlertIndicator(final Model124 parent, final int y) {
    final MV ls = new MV();
    GsGetLs(parent.coord2_14, ls);
    GTE.setTransforms(ls);
    GTE.perspectiveTransform(0, y - 64, 0);
    final float sx = GTE.getScreenX(2);
    final float sy = GTE.getScreenY(2);

    this.mapIndicator.renderAlertIndicator(GPU.getOffsetX() + this.alertIndicatorMetrics_800f64b0.x0_00 + sx, GPU.getOffsetY() + this.alertIndicatorMetrics_800f64b0.y0_04 + sy, 37, this.alertIndicatorMetrics_800f64b0.u0_08, this.alertIndicatorMetrics_800f64b0.v0_0c);
  }

  @Method(0x800e49f0L)
  private boolean hasPlayerMoved(final MV mat) {
    //LAB_800e4a44
    final boolean moved = !flEq(this.prevPlayerPos_800c6ab0.x, mat.transfer.x) || !flEq(this.prevPlayerPos_800c6ab0.y, mat.transfer.y) || !flEq(this.prevPlayerPos_800c6ab0.z, mat.transfer.z);

    //LAB_800e4a4c
    final EncounterRateMode mode = CONFIG.getConfig(CoreMod.ENCOUNTER_RATE_CONFIG.get());

    final float dist = mode.modifyDistance(this.prevPlayerPos_800c6ab0.x - mat.transfer.x + (this.prevPlayerPos_800c6ab0.z - mat.transfer.z));

    if(dist < 9.0f) {
      //LAB_800e4a98
      this.encounterMultiplier_800c6abc = mode.walkModifier;
    } else {
      this.encounterMultiplier_800c6abc = mode.runModifier;
    }

    //LAB_800e4aa0
    this.prevPlayerPos_800c6ab0.set(mat.transfer);
    return moved;
  }

  @Method(0x800e4b20L)
  private boolean handleEncounters() {
    if(this.smapTicks_800c6ae0 < 15 * (3 - vsyncMode_8007a3b8) || Unpacker.getLoadingFileCount() != 0 || gameState_800babc8.indicatorsDisabled_4e3) {
      return false;
    }

    this.ticksUntilEncountersAreEnabled_800c6ae4++;

    if(this.ticksUntilEncountersAreEnabled_800c6ae4 < 0) {
      return false;
    }

    // The first condition is to fix what we believe is caused by menus loading too fast in SC. Submaps still take several frames to initialize,
    // and if you spam triangle and escape immediately after the post-combat screen it's possible to get into this method when index_80052c38 is
    // still set to -1. See #304 for more details.
    if(this.collisionGeometry_800cbe08.getCollisionAndTransitionInfo(collidedPrimitiveIndex_80052c38) != 0) {
      return false;
    }

    //LAB_800e4bc0
    if(!this.isScriptLoaded(0)) {
      return false;
    }

    if(!this.hasPlayerMoved(this.sobjs_800c6880[0].innerStruct_00.model_00.coord2_14.coord)) {
      return false;
    }

    this.encounterAccumulator_800c6ae8 += Math.round(this.submap.getEncounterRate() * this.encounterMultiplier_800c6abc);

    if(this.encounterAccumulator_800c6ae8 <= 0x1400 * (3 - vsyncMode_8007a3b8)) {
      return false;
    }

    // Start combat
    this.submap.generateEncounter();

    if(Config.combatStage()) {
      battleStage_800bb0f4 = Config.getCombatStage();
    }

    return true;
  }

  @Method(0x800e4d00L)
  private void restoreSubmapAfterBattleOrSetPositionToCollisionPrimitive(final int collisionPrimitiveIndex) {
    if(this.restoreSubmapAfterBattle(this.playerPositionWhenLoadingSubmap_800c6ac0)) {
      this.playerPositionRestoreMode_800f7e24 = 1;
    } else {
      //LAB_800e4d34
      this.collisionGeometry_800cbe08.getMiddleOfCollisionPrimitive(collisionPrimitiveIndex, this.playerPositionWhenLoadingSubmap_800c6ac0.transfer);
      this.playerPositionRestoreMode_800f7e24 = 2;
    }

    //LAB_800e4d74
  }

  @Method(0x800e4d88L)
  private void restoreOrSetPlayerPosition(final int collisionPrimitiveIndexForPosition, final Model124 model) {
    if(this.playerPositionRestoreMode_800f7e24 == 0) {
      //LAB_800e4e20
      this.collisionGeometry_800cbe08.getMiddleOfCollisionPrimitive(collisionPrimitiveIndexForPosition, model.coord2_14.coord.transfer);
    } else if(this.playerPositionRestoreMode_800f7e24 == 1) {
      model.coord2_14.coord.set(this.playerPositionWhenLoadingSubmap_800c6ac0);
    } else {
      //LAB_800e4e04
      model.coord2_14.coord.transfer.set(this.playerPositionWhenLoadingSubmap_800c6ac0.transfer);
    }

    //LAB_800e4e18
    this.playerPositionRestoreMode_800f7e24 = 0;

    //LAB_800e4e4c
  }

  @Method(0x800e4ff4L)
  private void resetLatches() {
    for(int i = 0; i < this.latchList_800c6aec.size(); i++) {
      this.latchList_800c6aec.get(i).tick();
    }
  }

  @Method(0x800e5084L)
  private CountdownLatch addLatch() {
    final CountdownLatch latch = new CountdownLatch();
    this.latchList_800c6aec.add(latch);
    return latch;
  }

  @Method(0x800e5104L)
  private void loadAndRenderSubmapModelAndEffects(final int collisionGeometryIndexForInitialPlayerPosition, final MapTransitionData4c mapTransitionData) {
    this.executeSubmapMediaLoadingStage(collisionGeometryIndexForInitialPlayerPosition);

    if(this.sobjs_800c6880[0] != null) {
      mapTransitionData.run(this.sobjs_800c6880[0].innerStruct_00);
    }

    this.smapTicks_800c6ae0++;

    if(gameState_800babc8.indicatorsDisabled_4e3) {
      this.ticksUntilEncountersAreEnabled_800c6ae4 = -30 * (3 - vsyncMode_8007a3b8);
    }

    //LAB_800e5184
    this.resetLatches();
    this.collisionGeometry_800cbe08.tick();

    if(submapFullyLoaded_800bd7b4) {
      this.renderSubmap();
    }
  }

  @Method(0x800e519cL)
  private void renderEnvironment() {
    //LAB_800e51e8
    final MV[] matrices = new MV[this.sobjCount_800c6730];
    for(int i = 0; i < this.sobjCount_800c6730; i++) {
      if(!this.isScriptLoaded(i)) {
        return;
      }

      matrices[i] = this.sobjs_800c6880[i].innerStruct_00.model_00.coord2_14.coord;
    }

    //LAB_800e5234
    this.submap.drawEnv(matrices);
    this.renderCollisionDebug();
  }

  private void renderCollisionDebug() {
    if(enableCollisionDebug) {
      if(this.collisionGeometry_800cbe08.debugObj == null) {
        final List<Vector3f> vertices = new ArrayList<>();

        final PolyBuilder builder = new PolyBuilder("Collision Model", GL_TRIANGLE_STRIP);
        builder.translucency(Translucency.HALF_B_PLUS_HALF_F);

        final PolyBuilder lines = new PolyBuilder("Collision Normals Probably", GL_LINES);

        for(int i = 0; i < this.collisionGeometry_800cbe08.primitiveCount_0c; i++) {
          final CollisionPrimitiveInfo0c primitiveInfo = this.collisionGeometry_800cbe08.primitiveInfo_14[i];
          final TmdObjTable1c.Primitive primitive = this.collisionGeometry_800cbe08.getPrimitiveForOffset(primitiveInfo.primitiveOffset_04  );
          final int packetOffset = primitiveInfo.primitiveOffset_04 - primitive.offset();
          final int packetIndex = packetOffset / (primitive.width() + 4);
          final int remainder = packetOffset % (primitive.width() + 4);
          final byte[] packet = primitive.data()[packetIndex];

          for(int vertexIndex = 0; vertexIndex < primitiveInfo.vertexCount_00; vertexIndex++) {
            final Vector3f vertex = this.collisionGeometry_800cbe08.verts_04[IoHelper.readUShort(packet, remainder + 2 + vertexIndex * 2)];
            builder.addVertex(vertex);
            vertices.add(vertex);

            final Vector2f normals = new Vector2f(this.collisionGeometry_800cbe08.vertexInfo_18[vertexIndex].x_00, this.collisionGeometry_800cbe08.vertexInfo_18[vertexIndex].z_02)
              .normalize()
              .mul(10.0f);

            lines
              .addVertex(vertex)
              .addVertex(vertex.add(normals.x, 0.0f, normals.y, new Vector3f()));
          }
        }

        this.collisionGeometry_800cbe08.debugObj = builder.build();
        this.collisionGeometry_800cbe08.debugLines = lines.build();
        this.collisionGeometry_800cbe08.debugVertices = vertices.toArray(Vector3f[]::new);
      }

      final Vector2f transformed = new Vector2f();
      final Vector3f middle = new Vector3f();

      final MV lw = new MV();
      final MV ls = new MV();
      GsGetLws(this.collisionGeometry_800cbe08.dobj2Ptr_20.coord2_04, lw, ls);
      GTE.setTransforms(ls);

      for(int i = 0; i < this.collisionGeometry_800cbe08.primitiveCount_0c; i++) {
        final CollisionPrimitiveInfo0c primitiveInfo = this.collisionGeometry_800cbe08.primitiveInfo_14[i];

        final RenderEngine.QueuedModel model = RENDERER.queueModel(this.collisionGeometry_800cbe08.debugObj, lw)
          .vertices(primitiveInfo.vertexInfoOffset_02, primitiveInfo.vertexCount_00)
          .screenspaceOffset(this.screenOffset_800cb568.x + 8, -this.screenOffset_800cb568.y)
          .depthOffset(-1.0f)
        ;

        if(!primitiveInfo.flatEnoughToWalkOn_01) {
          model.colour(0.5f, 0.0f, 0.0f);
        }

        if((this.collisionGeometry_800cbe08.getCollisionAndTransitionInfo(i) & 0x1) != 0) {
          model.colour(0.5f, 0.0f, 1.0f);
        }

        if((this.collisionGeometry_800cbe08.getCollisionAndTransitionInfo(i) & 0x2) != 0) {
          model.colour(1.0f, 0.5f, 0.0f);
        }

        if((this.collisionGeometry_800cbe08.getCollisionAndTransitionInfo(i) & 0x4) != 0) {
          model.colour(1.0f, 1.0f, 0.0f);
        }

        if(!this.isWalkable(i)) {
          model.colour(1.0f, 0.0f, 0.0f);
        }

        if((this.collisionGeometry_800cbe08.getCollisionAndTransitionInfo(i) & 0x10) != 0) {
          model.colour(0.0f, 1.0f, 0.0f);
        }

        if((this.collisionGeometry_800cbe08.getCollisionAndTransitionInfo(i) & 0x20) != 0) {
          model.colour(0.0f, 1.0f, 1.0f);
        }

        for(int sobjIndex = 0; sobjIndex < this.sobjCount_800c6730; sobjIndex++) {
          if(this.sobjs_800c6880[sobjIndex].innerStruct_00.collidedPrimitiveIndex_16c == i) {
            model.colour(0.0f, 0.0f, sobjIndex == 0 ? 1.0f : 0.5f);
          }
        }

        this.submap.applyCollisionDebugColour(i, model);

        if(this.sobjs_800c6880[0].innerStruct_00.collidedPrimitiveIndex_16c != -1) {
          final CollisionPrimitiveInfo0c collidedPrimitive = this.collisionGeometry_800cbe08.primitiveInfo_14[this.sobjs_800c6880[0].innerStruct_00.collidedPrimitiveIndex_16c];

          RENDERER.queueModel(this.collisionGeometry_800cbe08.debugLines)
            .colour(1.0f, 0.0f, 0.0f)
            .screenspaceOffset(this.screenOffset_800cb568.x + 8, -this.screenOffset_800cb568.y)
            .vertices(collidedPrimitive.vertexInfoOffset_02 * 2, collidedPrimitive.vertexCount_00 * 2)
            .depthOffset(-1.0f);
        }

//        this.collisionGeometry_800cbe08.getMiddleOfCollisionPrimitive(i, middle);
//        this.transformVertex(transformed, middle);
//        final LodString text = new LodString(Integer.toString(i));
//        renderText(text, transformed.x + centreScreenX_1f8003dc - textWidth(text) / 2.0f, transformed.y + centreScreenY_1f8003de - 6, TextColour.LIME, 0);
      }
    } else if(this.collisionGeometry_800cbe08.debugObj != null) {
      this.collisionGeometry_800cbe08.debugObj.delete();
      this.collisionGeometry_800cbe08.debugObj = null;
      this.collisionGeometry_800cbe08.debugLines.delete();
      this.collisionGeometry_800cbe08.debugLines = null;
      this.collisionGeometry_800cbe08.debugVertices = null;
    }
  }

  /** Restores camera when returning from battle */
  @Method(0x800e5264L)
  private boolean restoreSubmapAfterBattle(final MV mat) {
    if(!this.submap.isReturningToSameMapAfterBattle()) {
      this.returnedToSameSubmapAfterBattle_800cb448 = false;
      return false;
    }

    //LAB_800e5294
    if(!shouldRestoreCameraPosition_80052c40) {
      return false;
    }

    //LAB_800e52b0
    this.setScreenOffsetIfNotSet(1, screenOffsetBeforeBattle_800bed50.x, screenOffsetBeforeBattle_800bed50.y);

    mat.set(playerPositionBeforeBattle_800bed30);

    shouldRestoreCameraPosition_80052c40 = false;
    this.returnedToSameSubmapAfterBattle_800cb448 = true;

    //LAB_800e5320
    return true;
  }

  @Method(0x800e5518L)
  private boolean isScriptLoaded(final int index) {
    return this.sobjs_800c6880[index] != null;
  }

  /**
   * Also does things like open menus using special values. If cut >= 0x800, plays FMV (cut - 0x800) and transitions to engine state newScene once FMV is finished.
   *
   * <ul>
   *   <li>Scene 0x3fa - char swap</li>
   *   <li>Scene 0x3fb - return to title</li>
   *   <li>Scene 0x3fc - too many items</li>
   *   <li>Scene 0x3fd - save at end of chapter</li>
   *   <li>Scene 0x3fe - shop</li>
   *   <li>Scene 0x3ff - inventory</li>
   * </ul>
   */
  @Method(0x800e5534L)
  public boolean mapTransition(final int newCut, final int newScene) {
    if(this.smapLoadingStage_800cb430 != SubmapState.RENDER_SUBMAP_12) {
      return false;
    }

    if(this.smapTicks_800c6ae0 < 3 * (3 - vsyncMode_8007a3b8)) {
      return false;
    }

    if(this._800f7e4c || (loadedDrgnFiles_800bcf78.get() & 0x82) != 0) {
      return false;
    }

    if(this.smapTicks_800c6ae0 > 15 * (3 - vsyncMode_8007a3b8)) {
      this.returnedToSameSubmapAfterBattle_800cb448 = false;
    }

    submapFullyLoaded_800bd7b4 = false;

    if(this.mapTransitionTicks_800cab28 == 0) {
      if(fullScreenEffect_800bb140._24 == 0) {
        startFadeEffect(1, 10);
        this.mapTransitionTicks_800cab28++;
      }
    } else {
      this.mapTransitionTicks_800cab28++;
    }

    this._800f7e4c = true;

    if(newCut > 0x7ff) {
      this.fmvIndex_800bf0dc = newCut - 0x800;
      this.afterFmvLoadingStage_800bf0ec = EngineStateEnum.values()[newScene];
      this.smapLoadingStage_800cb430 = SubmapState.TRANSITION_TO_FMV_21;
      return true;
    }

    if(newCut >= 0 && newCut < 2) {
      this.smapLoadingStage_800cb430 = SubmapState.TRANSITION_TO_WORLD_MAP_18;
      return true;
    }

    if(newCut > -1) {
      submapCut_80052c30 = newCut;
      submapScene_80052c34 = newScene;
      this.smapLoadingStage_800cb430 = SubmapState.CHANGE_SUBMAP_4;
      submapCutForSave_800cb450 = newCut;
      return true;
    }

    if(newScene == 0x3fa) {
      SCRIPTS.pause();
      whichMenu_800bdc38 = WhichMenu.INIT_CHAR_SWAP_MENU_21;
      this.smapLoadingStage_800cb430 = SubmapState.LOAD_MENU_13;
      submapCutForSave_800cb450 = submapCut_80052c30;
      return true;
    }

    if(newScene == 0x3fb) {
      SCRIPTS.pause();
      this.smapLoadingStage_800cb430 = SubmapState.TRANSITION_TO_TITLE_20;
      return true;
    }

    if(newScene == 0x3fc) {
      SCRIPTS.pause();
      whichMenu_800bdc38 = WhichMenu.INIT_TOO_MANY_ITEMS_MENU_31;
      this.smapLoadingStage_800cb430 = SubmapState.LOAD_MENU_13;
      return true;
    }

    if(newScene == 0x3fd) {
      SCRIPTS.pause();
      this.submapChapterDestinations_800f7e2c[0].submapScene_04 = collidedPrimitiveIndex_80052c38;
      collidedPrimitiveIndex_80052c38 = this.submapChapterDestinations_800f7e2c[gameState_800babc8.chapterIndex_98].submapScene_04;
      submapCutForSave_800cb450 = this.submapChapterDestinations_800f7e2c[gameState_800babc8.chapterIndex_98].submapCut_00;
      this.mapTransitionData_800cab24.clear();
      whichMenu_800bdc38 = WhichMenu.INIT_SAVE_GAME_MENU_16;
      this.smapLoadingStage_800cb430 = SubmapState.LOAD_MENU_13;
      return true;
    }

    if(newScene == 0x3fe) {
      SCRIPTS.pause();
      whichMenu_800bdc38 = WhichMenu.INIT_SHOP_MENU_6;
      this.smapLoadingStage_800cb430 = SubmapState.LOAD_MENU_13;
      return true;
    }

    if(newScene == 0x3ff) {
      SCRIPTS.pause();
      submapCutForSave_800cb450 = submapCut_80052c30;
      whichMenu_800bdc38 = WhichMenu.INIT_INVENTORY_MENU_1;
      this.smapLoadingStage_800cb430 = SubmapState.LOAD_MENU_13;
      return true;
    }

    final int encounterId;
    if(newScene == 0) {
      encounterId = encounterId_800bb0f8;
    } else {
      if(newScene > 0x1ff) {
        SCRIPTS.pause();
        return true;
      }

      encounterId = newScene;
    }

    encounterId_800bb0f8 = encounterId;

    if(this.isScriptLoaded(0)) {
      final SubmapObject210 sobj = this.sobjs_800c6880[0].innerStruct_00;

      screenOffsetBeforeBattle_800bed50.set(this.screenOffset_800cb568);
      this.submap.storeStateBeforeBattle();
      playerPositionBeforeBattle_800bed30.set(sobj.model_00.coord2_14.coord);
      shouldRestoreCameraPosition_80052c40 = true;
    }

    SCRIPTS.pause();
    this.smapLoadingStage_800cb430 = SubmapState.TRANSITION_TO_COMBAT_19;
    return true;
  }

  @Override
  @Method(0x800e5914L)
  public void tick() {
    //LAB_800e5a30
    //LAB_800e5a34
    if(pregameLoadingStage_800bb10c == 0) {
      pregameLoadingStage_800bb10c = 1;
      this.currentSubmapScene_800caaf8 = submapScene_80052c34;
      submapEnvState_80052c44 = SubmapEnvState.CHECK_TRANSITIONS_1_2;
      this.smapLoadingStage_800cb430 = SubmapState.INIT_0;
    }

    //LAB_800e5ac4
    switch(this.smapLoadingStage_800cb430) {
      case INIT_0 -> {
        vsyncMode_8007a3b8 = 1;
        SCRIPTS.setFramesPerTick(2);

        srand((int)System.nanoTime());
        resizeDisplay(384, 240);

        //LAB_800e5b2c
        submapEnvState_80052c44 = SubmapEnvState.CHECK_TRANSITIONS_1_2;
        this.encounterAccumulator_800c6ae8 = 0;
        this.smapLoadingStage_800cb430 = SubmapState.LOAD_NEWROOT_1;
      }

      case LOAD_NEWROOT_1 -> {
        loadFile("\\SUBMAP\\NEWROOT.RDT", data -> this.newrootPtr_800cab04 = new NewRootStruct(data));
        loadDir("\\SUBMAP\\savepoint", files -> {
          this.savepointAnm1 = new AnmFile(files.get(0));
          this.savepointAnm2 = new AnmFile(files.get(1));
          this.savepointTmd = new CContainer("Savepoint", files.get(2));
          this.savepointAnimation = new TmdAnimationFile(files.get(3));
          this.dustTmd = new CContainer("Dust", files.get(4));
          this.dustAnimation = new TmdAnimationFile(files.get(5));
        });
        this.smapLoadingStage_800cb430 = SubmapState.WAIT_FOR_NEWROOT_2;
      }

      case WAIT_FOR_NEWROOT_2 -> {
        if(Unpacker.getLoadingFileCount() == 0) {
          this.smapLoadingStage_800cb430 = SubmapState.LOAD_ENVIRONMENT_3;
        }
      }

      case LOAD_ENVIRONMENT_3 -> {
        this.mapTransitionTicks_800cab28 = 0;
        submapEnvState_80052c44 = SubmapEnvState.CHECK_TRANSITIONS_1_2;
        this.currentSubmapScene_800caaf8 = submapScene_80052c34;

        this.submap = new RetailSubmap(submapCut_80052c30, this.newrootPtr_800cab04, this.screenOffset_800cb568, this.collisionGeometry_800cbe08);
        this.submap.loadEnv(() -> this.smapLoadingStage_800cb430 = SubmapState.START_LOADING_MEDIA_10);
        this.smapLoadingStage_800cb430 = SubmapState.WAIT_FOR_ENVIRONMENT;
      }

      case CHANGE_SUBMAP_4 -> {
        this.loadAndRenderSubmapModelAndEffects(this.currentSubmapScene_800caaf8, this.mapTransitionData_800cab24);
        this.smapLoadingStage_800cb430 = SubmapState.TRANSITION_TO_SUBMAP_17;
      }

      case START_LOADING_MEDIA_10 -> {
        worldToScreenMatrix_800c3548.transpose(this.screenToWorldMatrix_800cbd40);

        this.submap.prepareEnv();
        this.restoreSubmapAfterBattleOrSetPositionToCollisionPrimitive(submapScene_80052c34);
        this.initCamera(submapScene_80052c34);
        this.clearSubmapFlags();

        this.mediaLoadingStage_800c68e4 = SubmapMediaState.LOAD_SHADOW_AND_RESET_LIGHTING_0;
        this.executeSubmapMediaLoadingStage(this.currentSubmapScene_800caaf8);
        this.smapLoadingStage_800cb430 = SubmapState.FINISH_LOADING_AND_FADE_IN_11;
      }

      case FINISH_LOADING_AND_FADE_IN_11 -> {
        this.executeSubmapMediaLoadingStage(this.currentSubmapScene_800caaf8);

        // Wait for media to finish loading
        if(this.mediaLoadingStage_800c68e4 != SubmapMediaState.DONE) {
          return;
        }

        if(this.isScriptLoaded(0)) {
          this.sobjs_800c6880[0].innerStruct_00.collidedPrimitiveIndex_16c = this.currentSubmapScene_800caaf8;
        }

        //LAB_800e5e94
        loadingNewGameState_800bdc34 = false;
        this.submap.loadMapTransitionData(this.mapTransitionData_800cab24);
        this.submap.finishLoading();
        startFadeEffect(2, 10);
        SCRIPTS.resume();
        this.smapTicks_800c6ae0 = 0;
        this.smapLoadingStage_800cb430 = SubmapState.RENDER_SUBMAP_12;
      }

      case RENDER_SUBMAP_12 -> {
        submapEnvState_80052c44 = SubmapEnvState.RENDER_AND_CHECK_TRANSITIONS_0;

        this.loadAndRenderSubmapModelAndEffects(this.currentSubmapScene_800caaf8, this.mapTransitionData_800cab24);

        if(Input.pressedThisFrame(InputAction.BUTTON_NORTH) && !gameState_800babc8.indicatorsDisabled_4e3) {
          this.mapTransition(-1, 0x3ff); // Open inv
        }
      }

      case LOAD_MENU_13 -> {
        this.loadAndRenderSubmapModelAndEffects(this.currentSubmapScene_800caaf8, this.mapTransitionData_800cab24);

        submapFullyLoaded_800bd7b4 = false;

        if(this.mapTransitionTicks_800cab28 != 0 || fullScreenEffect_800bb140._24 == 0) {
          if(fullScreenEffect_800bb140._24 == 0) {
            startFadeEffect(1, 10);
          }

          //LAB_800e5fa4
          this.mapTransitionTicks_800cab28++;

          if(this.mapTransitionTicks_800cab28 <= 10) {
            break;
          }
        }

        //LAB_800e5fc0
        this.smapLoadingStage_800cb430 = SubmapState.RENDER_MENU_14;
        this.mapTransitionTicks_800cab28 = 0;
      }

      case RENDER_MENU_14 -> {
        final WhichMenu menu = whichMenu_800bdc38; // copy menu state since some of these functions may change it
        submapEnvState_80052c44 = SubmapEnvState.CHECK_TRANSITIONS_1_2;

        if(whichMenu_800bdc38 != WhichMenu.NONE_0) {
          loadAndRenderMenus();

          if(whichMenu_800bdc38 == WhichMenu.QUIT) {
            this.smapLoadingStage_800cb430 = SubmapState.RENDER_SUBMAP_12;
            this._800f7e4c = false;
            this.mapTransition(-1, 0x3fb);
            drgnBinIndex_800bc058 = 1;
            break;
          }

          if(whichMenu_800bdc38 != WhichMenu.NONE_0) {
            break;
          }
        }

        //LAB_800e6018
        switch(menu) {
          case UNLOAD_INVENTORY_MENU_5:
            if(gameState_800babc8.isOnWorldMap_4e4) {
              this.smapLoadingStage_800cb430 = SubmapState.TRANSITION_TO_WORLD_MAP_18;
              this._800f7e4c = false;
              break;
            }

            // Fall through

          case UNLOAD_CHAR_SWAP_MENU_25:
          case UNLOAD_TOO_MANY_ITEMS_MENU_35:
          case UNLOAD_SHOP_MENU_10:
            this.smapLoadingStage_800cb430 = SubmapState.UNLOAD_MENU_15;
            break;

          case UNLOAD_SAVE_GAME_MENU_20:
            this.smapLoadingStage_800cb430 = SubmapState.RENDER_SUBMAP_12;
            this._800f7e4c = false;
            this.mapTransition(this.submapChapterDestinations_800f7e2c[gameState_800babc8.chapterIndex_98].submapCut_00, this.submapChapterDestinations_800f7e2c[gameState_800babc8.chapterIndex_98].submapCut_00);
            collidedPrimitiveIndex_80052c38 = this.submapChapterDestinations_800f7e2c[0].submapScene_04;
            break;
        }
      }

      case UNLOAD_MENU_15 -> {
        submapEnvState_80052c44 = SubmapEnvState.RENDER_AND_CHECK_TRANSITIONS_0;
        this.loadAndRenderSubmapModelAndEffects(this.currentSubmapScene_800caaf8, this.mapTransitionData_800cab24);
        SCRIPTS.resume();
        this._800f7e4c = false;
        this.smapLoadingStage_800cb430 = SubmapState.RENDER_SUBMAP_12;

        if(loadingNewGameState_800bdc34) {
          this.mapTransition(submapCut_80052c30, submapScene_80052c34);
        }
      }

      case TRANSITION_TO_SUBMAP_17 -> {
        this.loadAndRenderSubmapModelAndEffects(this.currentSubmapScene_800caaf8, this.mapTransitionData_800cab24);

        if(this.isScriptLoaded(0)) {
          this.sobjs_800c6880[0].innerStruct_00.disableAnimation_12a = true;
        }

        //LAB_800e61bc
        submapFullyLoaded_800bd7b4 = false;

        if(this.mapTransitionTicks_800cab28 != 0 || fullScreenEffect_800bb140._24 == 0) {
          if(fullScreenEffect_800bb140._24 == 0) {
            startFadeEffect(1, 10);
          }

          //LAB_800e61fc
          this.mapTransitionTicks_800cab28++;

          if(this.mapTransitionTicks_800cab28 <= 10) {
            break;
          }
        }

        //LAB_800e6218
        this.smapLoadingStage_800cb430 = SubmapState.LOAD_ENVIRONMENT_3;

        //LAB_800e6248
        if(this.returnedToSameSubmapAfterBattle_800cb448) {
          submapEnvState_80052c44 = SubmapEnvState.UNLOAD_3;
        } else {
          submapEnvState_80052c44 = SubmapEnvState.RENDER_AND_UNLOAD_4_5;
        }

        //LAB_800e624c
        //LAB_800e6250
        this._800f7e4c = false;
      }

      case TRANSITION_TO_WORLD_MAP_18 -> {
        this.loadAndRenderSubmapModelAndEffects(this.currentSubmapScene_800caaf8, this.mapTransitionData_800cab24);

        submapFullyLoaded_800bd7b4 = false;

        if(this.mapTransitionTicks_800cab28 != 0 || fullScreenEffect_800bb140._24 == 0) {
          if(fullScreenEffect_800bb140._24 == 0) {
            startFadeEffect(1, 10);
          }

          //LAB_800e62b0
          this.mapTransitionTicks_800cab28++;

          if(this.mapTransitionTicks_800cab28 <= 10) {
            break;
          }
        }

        //LAB_800e62cc
        engineStateOnceLoaded_8004dd24 = EngineStateEnum.WORLD_MAP_08;
        pregameLoadingStage_800bb10c = 0;
        submapEnvState_80052c44 = SubmapEnvState.RENDER_AND_UNLOAD_4_5;
        this._800f7e4c = false;
        SCRIPTS.resume();
      }

      case TRANSITION_TO_COMBAT_19 -> {
        this.loadAndRenderSubmapModelAndEffects(this.currentSubmapScene_800caaf8, this.mapTransitionData_800cab24);
        submapEnvState_80052c44 = SubmapEnvState.RENDER_AND_UNLOAD_4_5;
        engineStateOnceLoaded_8004dd24 = EngineStateEnum.COMBAT_06;
        pregameLoadingStage_800bb10c = 0;
        this._800f7e4c = false;
        SCRIPTS.resume();
      }

      case TRANSITION_TO_TITLE_20 -> {
        this.loadAndRenderSubmapModelAndEffects(this.currentSubmapScene_800caaf8, this.mapTransitionData_800cab24);

        submapFullyLoaded_800bd7b4 = false;

        if(this.mapTransitionTicks_800cab28 != 0 || fullScreenEffect_800bb140._24 == 0) {
          if(fullScreenEffect_800bb140._24 == 0) {
            startFadeEffect(1, 10);
          }

          //LAB_800e643c
          this.mapTransitionTicks_800cab28++;

          if(this.mapTransitionTicks_800cab28 <= 10) {
            break;
          }
        }

        //LAB_800e6458
        FUN_8002a9c0();
        engineStateOnceLoaded_8004dd24 = EngineStateEnum.TITLE_02;
        pregameLoadingStage_800bb10c = 0;

        //LAB_800e6484
        submapEnvState_80052c44 = SubmapEnvState.RENDER_AND_UNLOAD_4_5;

        //LAB_800e6490
        this._800f7e4c = false;
        SCRIPTS.resume();
      }

      case TRANSITION_TO_FMV_21 -> {
        this.loadAndRenderSubmapModelAndEffects(this.currentSubmapScene_800caaf8, this.mapTransitionData_800cab24);

        submapFullyLoaded_800bd7b4 = false;

        if(this.mapTransitionTicks_800cab28 != 0 || fullScreenEffect_800bb140._24 == 0) {
          if(fullScreenEffect_800bb140._24 == 0) {
            startFadeEffect(1, 10);
          }

          //LAB_800e6394
          this.mapTransitionTicks_800cab28++;

          if(this.mapTransitionTicks_800cab28 <= 10) {
            break;
          }
        }

        //LAB_800e63b0
        submapEnvState_80052c44 = SubmapEnvState.RENDER_AND_UNLOAD_4_5;
        Fmv.playCurrentFmv(this.fmvIndex_800bf0dc, this.afterFmvLoadingStage_800bf0ec);
        pregameLoadingStage_800bb10c = 0;
        this._800f7e4c = false;
        SCRIPTS.resume();
      }
    }
  }

  /** Has to be done after scripts are ticked since the camera is attached to a sobj and it would use the position from the previous frame */
  @Override
  public void postScriptTick() {
    //LAB_80020f20
    this.renderEnvironmentAndHandleTransitions();
    this.setIndicatorStatusAndResetIndicatorTickCountOnReenable();
  }

  @Method(0x800e6798L)
  private boolean isWalkable(final int collisionPrimitiveIndex) {
    return (this.collisionGeometry_800cbe08.getCollisionAndTransitionInfo(collisionPrimitiveIndex) & 0x8) == 0;
  }

  @ScriptDescription("Transitions to another submap cut/scene (certain values have special meanings - FMVs, menus)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "submapCut", description = "The submap cut")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "submapScene", description = "The submap scene")
  @Method(0x800e67d4L)
  private FlowControl scriptMapTransition(final RunningScript<?> script) {
    final int scene = script.params_20[1].get();

    this.mapTransition(script.params_20[0].get(), scene);

    if(scene == 0x3fa || scene == 0x3fc || scene == 0x3fe || scene == 0x3ff) {
      return FlowControl.CONTINUE;
    }

    //LAB_800e6828
    return FlowControl.PAUSE_AND_REWIND;
  }

  @ScriptDescription("Either sets the mode for a subsequent call to scriptSetCameraOffset, or hides a submap foreground overlay. May be for revealing areas when walking through doors, for example.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "mode", description = "< 3, sets the mode for a subsequent call to scriptSetCameraOffset; 3 hides foreground object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "foregroundObjectIndex", description = "The foreground object index")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "out", description = "The value passed in for mode, unless mode is 3, in which case the mode variable isn't updated and the previous value is returned")
  @Method(0x800e683cL)
  private FlowControl scriptSetModeParamForNextCallToScriptSetCameraOffsetOrHideSubmapForegroundObject(final RunningScript<?> script) {
    if(script.params_20[0].get() < 3) {
      this.scriptSetOffsetMode_800f7e50 = script.params_20[0].get();
    }

    //LAB_800e686c
    if(script.params_20[0].get() == 3) {
      ((RetailSubmap)this.submap).setEnvForegroundPosition(1024, 1024, script.params_20[1].get()); // Hide foreground object
    }

    //LAB_800e688c
    script.params_20[2].set(this.scriptSetOffsetMode_800f7e50);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Gets the camera offset")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "x", description = "The camera X offset")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "y", description = "The camera Y offset")
  @Method(0x800e68b4L)
  private FlowControl scriptGetCameraOffset(final RunningScript<?> script) {
    script.params_20[0].set(this.screenOffset_800cb568.x);
    script.params_20[1].set(this.screenOffset_800cb568.y);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Sets the camera offset (mode is based on the last call to scriptSetModeParamForNextCallToScriptSetCameraOffsetOrHideSubmapForegroundObject)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "The camera X offset")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "The camera Y offset")
  @Method(0x800e6904L)
  private FlowControl scriptSetCameraOffset(final RunningScript<?> script) {
    final int x = script.params_20[0].get();
    final int y = script.params_20[1].get();
    final int mode = this.scriptSetOffsetMode_800f7e50;

    if(mode == 1 || mode == 2) {
      //LAB_800e695c
      this.setGeomOffsetIfNotSet(3 - vsyncMode_8007a3b8, x, y);
    }

    if(mode == 2 || mode == 3) {
      this.setScreenOffsetIfNotSet(3 - vsyncMode_8007a3b8, x, y);
    }

    //LAB_800e6988
    this.scriptSetOffsetMode_800f7e50 = 0;
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Gets collision and transition info")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "flags", description = "The collision and transition info")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "collisionPrimitiveIndex", description = "The index")
  @Method(0x800e69a4L)
  private FlowControl scriptGetCollisionAndTransitionInfo(final RunningScript<?> script) {
    script.params_20[0].set(this.collisionGeometry_800cbe08.getCollisionAndTransitionInfo(script.params_20[1].get()));
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("No-op")
  @Method(0x800e69e8L)
  private FlowControl FUN_800e69e8(final RunningScript<?> script) {
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Sets a collision primitive as blocked")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "collisionPrimitiveIndex", description = "The index")
  @Method(0x800e69f0L)
  private FlowControl scriptBlockCollisionPrimitive(final RunningScript<?> script) {
    this.collisionGeometry_800cbe08.setCollisionAndTransitionInfo(this.collisionGeometry_800cbe08.getCollisionAndTransitionInfo(script.params_20[0].get()) | 0x8);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Sets a collision primitive as unblocked")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "collisionPrimitiveIndex", description = "The index")
  @Method(0x800e6a28L)
  private FlowControl scriptUnblockCollisionPrimitive(final RunningScript<?> script) {
    this.collisionGeometry_800cbe08.setCollisionAndTransitionInfo(this.collisionGeometry_800cbe08.getCollisionAndTransitionInfo(script.params_20[0].get()) & 0xffff_fff7);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Sets the position of an environment foreground overlay")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "The new X position")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "The new Y position")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "overlayIndex", description = "The overlay index")
  @Method(0x800e6a64L)
  private FlowControl scriptSetEnvForegroundPosition(final RunningScript<?> script) {
    ((RetailSubmap)this.submap).setEnvForegroundPosition(script.params_20[0].get(), script.params_20[1].get(), script.params_20[2].get());
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Unknown, related to environment foreground overlays")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "mode", description = "The mode (maybe layering mode?)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "overlayIndex", description = "The overlay index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "The new Z position (only applies in modes 2 and 5")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "out", description = "The overlay's Z position")
  @Method(0x800e6aa0L)
  private FlowControl FUN_800e6aa0(final RunningScript<?> script) {
    script.params_20[3].set(((RetailSubmap)this.submap).FUN_800e7728(script.params_20[0].get(), script.params_20[1].get(), script.params_20[2].get()));
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Gets (and optionally sets) whether submap encounters are disabled")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.BOOL, name = "set", description = "True to disable encounters, false otherwise")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.BOOL, name = "apply", description = "If false, does not update value, only returns it")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "value", description = "The value of the submap flags (0x1 - encounters disabled)")
  @Method(0x800e6af0L)
  private FlowControl scriptGetSetEncountersDisabled(final RunningScript<?> script) {
    if(script.params_20[1].get() == 1) {
      if(script.params_20[0].get() != 0) {
        this.submapFlags_800f7e54 |= 0x1;
      } else {
        //LAB_800e6b34
        this.submapFlags_800f7e54 &= 0xffff_fffe;
      }
    }

    //LAB_800e6b48
    script.params_20[2].set(this.submapFlags_800f7e54);
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Gets the middle of a collision primitive")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "collisionPrimitiveIndex", description = "The collision primitive index")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "x", description = "The X position")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "y", description = "The Y position")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "z", description = "The Z position")
  @Method(0x800e6b64L)
  private FlowControl scriptGetCollisionPrimitivePos(final RunningScript<?> script) {
    final Vector3f pos = new Vector3f();
    this.collisionGeometry_800cbe08.getMiddleOfCollisionPrimitive(script.params_20[0].get(), pos);

    script.params_20[1].set(Math.round(pos.x));
    script.params_20[2].set(Math.round(pos.y));
    script.params_20[3].set(Math.round(pos.z));

    //LAB_800e6bc8
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("No-op")
  @Method(0x800e6bd8L)
  private FlowControl FUN_800e6bd8(final RunningScript<?> script) {
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Unknown usage, I think this calculates a Z sorting value for a submap object")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "z", description = "The calculated Z value")
  @Method(0x800e6be0L)
  private FlowControl FUN_800e6be0(final RunningScript<?> script) {
    final MV coord = this.sobjs_800c6880[script.params_20[0].get()].innerStruct_00.model_00.coord2_14.coord;
    script.params_20[1].set(Math.round((worldToScreenMatrix_800c3548.m02 * coord.transfer.x + worldToScreenMatrix_800c3548.m12 * coord.transfer.y + worldToScreenMatrix_800c3548.m22 * coord.transfer.z + worldToScreenMatrix_800c3548.transfer.z) / (1 << 16 - orderingTableBits_1f8003c0)));
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Unknown, seems to be a temporary X/Y camera offset for the next time the offset is calculated")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "The camera X offset")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "Y", description = "The camera Y offset")
  @Method(0x800e6cacL)
  private FlowControl FUN_800e6cac(final RunningScript<?> script) {
    ((RetailSubmap)this.submap).FUN_800e80e4(script.params_20[0].get(), script.params_20[1].get());
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Starts an FMV")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "fmvIndex", description = "The FMV index to play")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "engineStateAfterFmv", description = "The engine state to transition to after the FMV")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "submapCutAfterFmv", description = "The submap cut after the FMV")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "submapSceneAfterFmv", description = "The submap scene after the FMV")
  @Method(0x800e6ce0L)
  private FlowControl scriptStartFmv(final RunningScript<?> script) {
    this.mapTransition(script.params_20[0].get() + 0x800, script.params_20[1].get());
    submapCut_80052c30 = script.params_20[2].get();
    submapScene_80052c34 = script.params_20[3].get();
    submapCutForSave_800cb450 = submapCut_80052c30;
    return FlowControl.PAUSE_AND_REWIND;
  }

  @Method(0x800e6d4cL)
  private void clearSubmapFlags() {
    this.submapFlags_800f7e54 = 0;
  }

  @Method(0x800e7604L)
  private void setGeomOffsetIfNotSet(final int latchTicks, final int x, final int y) {
    if(this.geomOffsetLatch_800cbd3c.isOpen()) {
      this.geomOffsetLatch_800cbd3c.latch(latchTicks);
      GTE.setScreenOffset(x, y);
    }
  }

  @Method(0x800e7650L)
  private void setScreenOffsetIfNotSet(final int latchTicks, final int x, final int y) {
    // Added null check - bug in game code
    if(this.screenOffsetLatch_800cbd38 != null && this.screenOffsetLatch_800cbd38.isOpen()) {
      this.screenOffsetLatch_800cbd38.latch(latchTicks);
      this.screenOffset_800cb568.x = x;
      this.screenOffset_800cb568.y = y;
    }
  }

  @Method(0x800e7f00L)
  private void transformVertex(final Vector2f out, final Vector3f v0) {
    GTE.perspectiveTransform(v0);
    out.set(GTE.getScreenX(2), GTE.getScreenY(2));
  }

  @ScriptDescription("Sets collision metrics for a submap object (collision type unknown)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "horizontalSize", description = "The horizontal collision size")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "verticalSize", description = "The vertical collision size")
  @Method(0x800e06c4L)
  private FlowControl scriptSetSobjCollision1(final RunningScript<?> script) {
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00;
    sobj.collisionSizeHorizontal_1a0 = script.params_20[1].get();
    sobj.collisionSizeVertical_1a4 = script.params_20[2].get();
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Checks if a submap object is collided (collision type unknown)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.OUT, type = ScriptParam.Type.INT, name = "collided", description = "True if the submap object is collided")
  @Method(0x800e0710L)
  private FlowControl scriptGetSobjCollidedWith1(final RunningScript<?> script) {
    script.params_20[1].set(((SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[0].get()].innerStruct_00).collidedWithSobjIndex_19c);
    return FlowControl.CONTINUE;
  }

  @Method(0x800e8104L)
  private void setCameraPos(final int latchTicks, final Vector3f cameraPos) {
    if(this.screenOffsetLatch_800cbd38.isOpen()) {
      this.screenOffsetLatch_800cbd38.latch(latchTicks);

      final Vector2f transformed = new Vector2f();
      this.transformVertex(transformed, cameraPos);
      this.submap.calcGoodScreenOffset(transformed.x, transformed.y);
    }

    //LAB_800e8164
    this.setScreenOffsetIfNotSet(1, this.screenOffset_800cb568.x, this.screenOffset_800cb568.y);
    this.setGeomOffsetIfNotSet(1, this.screenOffset_800cb568.x, this.screenOffset_800cb568.y);
  }

  @Method(0x800e81a0L)
  private void initCamera(final int cameraCollisionPrimitiveIndex) {
    this.screenOffsetLatch_800cbd38 = this.addLatch();
    this.geomOffsetLatch_800cbd3c = this.addLatch();

    final Vector3f pos = new Vector3f();
    this.collisionGeometry_800cbe08.getMiddleOfCollisionPrimitive(cameraCollisionPrimitiveIndex, pos);
    this.setCameraPos(1, pos);
  }

  @Method(0x800e828cL)
  private void clearLatches() {
    this.latchList_800c6aec.clear();
    this.screenOffsetLatch_800cbd38 = null;
    this.geomOffsetLatch_800cbd3c = null;
  }

  @Method(0x800e82ccL)
  private void transformToWorldspace(final Vector3f out, final Vector3f in) {
    if(this.screenToWorldMatrix_800cbd40.m02 == 0.0f) {
      out.set(in);
    } else {
      //LAB_800e8318
      in.mul(this.screenToWorldMatrix_800cbd40, out);
    }

    //LAB_800e833c
  }

  @Method(0x800ea4c8L)
  private float smoothDartRotation() {
    if(this.firstMovement) {
      this.firstMovement = false;
      Arrays.fill(this.oldRotations_800f7f6c, this.collisionGeometry_800cbe08.dartRotationAfterCollision_800d1a84);
    }

    final int lastRotationIndex = java.lang.Math.floorMod(this.smapTicks_800c6ae0 - (3 - vsyncMode_8007a3b8), 4 * (3 - vsyncMode_8007a3b8));
    final int newRotationIndex = this.smapTicks_800c6ae0 % (4 * (3 - vsyncMode_8007a3b8));
    float rotationDelta = this.oldRotations_800f7f6c[lastRotationIndex] - this.collisionGeometry_800cbe08.dartRotationAfterCollision_800d1a84;

    final boolean positive;
    if(Math.abs(rotationDelta) > MathHelper.PI) {
      positive = rotationDelta > 0;
      rotationDelta = MathHelper.TWO_PI - Math.abs(rotationDelta);
    } else {
      //LAB_800ea628
      positive = rotationDelta <= 0;
      rotationDelta = Math.abs(rotationDelta);
    }

    //LAB_800ea63c
    if(rotationDelta > 0.125f * MathHelper.TWO_PI) { // 45 degrees
      rotationDelta /= 4.0f;
    }

    //LAB_800ea66c
    final float newRotation;
    if(!positive) {
      newRotation = this.oldRotations_800f7f6c[lastRotationIndex] - rotationDelta;
    } else {
      //LAB_800ea6a0
      newRotation = this.oldRotations_800f7f6c[lastRotationIndex] + rotationDelta;
    }

    //LAB_800ea6a4
    this.oldRotations_800f7f6c[newRotationIndex] = newRotation;

    //LAB_800ea6dc
    return newRotation;
  }

  /** Used in Snow Field (disk 3) */
  @Method(0x800ee20cL)
  private void handleSnow() {
    if(this.submapEffectsState_800f9eac == -1) {
      if(this.snowState_800f9e60 != 0) {
        this.snow_800d4bd8.deallocate();
        this.snow_800d4bd8 = null;
      }

      //LAB_800ee348
      this.snowState_800f9e60 = 0;
    } else {
      this.snow_800d4bd8.render();
    }
    //LAB_800ee354
  }

  @Method(0x800ef0f8L)
  private void tickAttachedSobjEffects(final Model124 model, final AttachedSobjEffectData40 data) {
    if(!flEq(data.transfer_1e.x, model.coord2_14.coord.transfer.x) || !flEq(data.transfer_1e.y, model.coord2_14.coord.transfer.y) || !flEq(data.transfer_1e.z, model.coord2_14.coord.transfer.z)) {
      //LAB_800ef154
      if(data.shouldRenderTmdDust_04) {
        if(data.tick_00 % (data.instantiationIntervalDust30 * (3 - vsyncMode_8007a3b8)) == 0) {
          final TmdTrailParticle20 inst = this.addTmdDustParticle(this.tmdTrail_800d4ec0);
          inst.tick_00 = 0;
          inst.maxTicks_18 = data.maxTicks_38;

          final int size = data.size_28;
          if(size < 0) {
            inst.size_08 = -size;
            inst.stepSize_04 = -inst.stepSize_04 / 20.0f;
          } else if(size > 0) {
            //LAB_800ef1e0
            inst.size_08 = 0.0f;
            inst.stepSize_04 = size / 20.0f;
          } else {
            //LAB_800ef214
            inst.size_08 = 0.0f;
            inst.stepSize_04 = 0.0f;
          }

          //LAB_800ef21c
          inst.transfer.set(model.coord2_14.coord.transfer);
        }
      }

      //LAB_800ef240
      if(data.shouldRenderFootprints_08) {
        if(data.tick_00 % (data.instantiationIntervalFootprints_34 * (3 - vsyncMode_8007a3b8)) == 0) {
          //LAB_800ef394
          final OrthoTrailParticle54 inst = this.addOrthoQuadTrailParticle(this.orthoQuadTrail);

          if(data.footprintMode_10 != 0) {
            //LAB_800ef3e8
            inst.renderMode_00 = 2;
            inst.textureIndex_02 = 3;

            //LAB_800ef3f8
          } else {
            inst.renderMode_00 = 0;
            inst.textureIndex_02 = data.textureIndexType1_1c;

            data.textureIndexType1_1c ^= 1;
          }

          //LAB_800ef3fc
          inst.x_18 = this.screenOffset_800cb568.x;
          inst.y_1c = this.screenOffset_800cb568.y;
          inst.tick_04 = 0;
          inst.maxTicks_06 = 150;

          final MV ls = new MV();
          GsGetLs(model.coord2_14, ls);
          GTE.setTransforms(ls);

          final int textureIndex = inst.textureIndex_02;
          if(textureIndex == 0) {
            //LAB_800ef4b4
            inst.z_4c = RotTransPers4(this.footprintQuadVertices_800d6b7c[4], this.footprintQuadVertices_800d6b7c[5], this.footprintQuadVertices_800d6b7c[6], this.footprintQuadVertices_800d6b7c[7], inst.sxy0_20, inst.sxy1_28, inst.sxy2_30, inst.sxy3_38);
          } else if(textureIndex == 1) {
            //LAB_800ef484
            //LAB_800ef4b4
            inst.z_4c = RotTransPers4(this.footprintQuadVertices_800d6b7c[8], this.footprintQuadVertices_800d6b7c[9], this.footprintQuadVertices_800d6b7c[10], this.footprintQuadVertices_800d6b7c[11], inst.sxy0_20, inst.sxy1_28, inst.sxy2_30, inst.sxy3_38);
          } else if(textureIndex == 3) {
            //LAB_800ef4a0
            //LAB_800ef4b4
            inst.z_4c = RotTransPers4(this.footprintQuadVertices_800d6b7c[0], this.footprintQuadVertices_800d6b7c[1], this.footprintQuadVertices_800d6b7c[2], this.footprintQuadVertices_800d6b7c[3], inst.sxy0_20, inst.sxy1_28, inst.sxy2_30, inst.sxy3_38);
          }

          //LAB_800ef4ec
          if(inst.z_4c < 41) {
            inst.z_4c = 41;
          }

          //LAB_800ef504
          inst.stepBrightness_40 = 0.5f / 30;
          inst.brightness_48 = 0.5f;
        }
      }

      //LAB_800ef520
      if(data.shouldRenderOrthoDust_0c) {
        if(data.tick_00 % (data.instantiationIntervalDust30 * (3 - vsyncMode_8007a3b8)) == 0) {
          final OrthoTrailParticle54 inst = this.addOrthoQuadTrailParticle(this.orthoQuadTrail);
          inst.renderMode_00 = 1;
          inst.textureIndex_02 = 2;
          inst.x_18 = this.screenOffset_800cb568.x;
          inst.y_1c = this.screenOffset_800cb568.y;

          final Vector3f vert0 = new Vector3f(-data.size_28, 0.0f, -data.size_28);
          final Vector3f vert1 = new Vector3f( data.size_28, 0.0f, -data.size_28);
          final Vector3f vert2 = new Vector3f(-data.size_28, 0.0f,  data.size_28);
          final Vector3f vert3 = new Vector3f( data.size_28, 0.0f,  data.size_28);

          inst.tick_04 = 0;
          inst.maxTicks_06 = (short)data.maxTicks_38;

          final MV ls = new MV();
          GsGetLs(model.coord2_14, ls);
          GTE.setTransforms(ls);

          //TODO The real code actually passes the same reference for sxyz 1 and 2, is that a bug?
          inst.z_4c = RotTransPers4(vert0, vert1, vert2, vert3, inst.sxy0_20, inst.sxy1_28, inst.sxy2_30, inst.sxy3_38);

          if(inst.z_4c < 41) {
            inst.z_4c = 41;
          }

          //LAB_800ef6a0
          final float halfSize = (inst.sxy3_38.x - inst.sxy0_20.x) / 2.0f;
          inst.size_08 = halfSize;
          inst.sizeStep_0c = halfSize / data.maxTicks_38;

          inst.z0_26 = (inst.sxy3_38.x + inst.sxy0_20.x) / 2.0f;
          inst.z1_2e = (inst.sxy3_38.y + inst.sxy0_20.y) / 2.0f;

          inst.stepBrightness_40 = 0.5f / data.maxTicks_38;
          inst.brightness_48 = 0.5f;
        }
      }

      //LAB_800ef728
      if(data.shouldRenderLawPodTrail_18) {
        if(!this.unloadSubmapParticles_800c6870) {
          this.tickLawPodTrail(model, data);
        }
      }
    }

    //LAB_800ef750
    data.transfer_1e.set(model.coord2_14.coord.transfer);
    data.tick_00++;
  }

  @Method(0x800ef798L)
  private void renderTmdTrail() {
    TmdTrailParticle20 prev = this.tmdTrail_800d4ec0;
    TmdTrailParticle20 inst = prev.next_1c;

    //LAB_800ef7c8
    while(inst != null) {
      if(inst.tick_00 >= inst.maxTicks_18 * (3 - vsyncMode_8007a3b8)) {
        prev.next_1c = inst.next_1c;
        inst = prev.next_1c;
      } else {
        //LAB_800ef804
        inst.transfer.y -= 1.0f / (3 - vsyncMode_8007a3b8);

        this.tmdDustModel_800d4d40.coord2_14.coord.transfer.set(inst.transfer);

        // In retail, the shrinking tail of the snow slide snow cloud animation occurs because size_08 is in .12
        // and RotMatrixXyz writes everything as shorts, which turns scale negative above 0x7fff (>=8.0f).
        // To implement this more smoothly, stepSize_04 is switched to negative once size_08 reaches 8.0f.
        if(inst.size_08 >= 8.0f) {
          inst.stepSize_04 = -inst.stepSize_04;
        }
        inst.size_08 += inst.stepSize_04 / (3 - vsyncMode_8007a3b8);

        this.tmdDustModel_800d4d40.coord2_14.transforms.scale.set(inst.size_08, inst.size_08, inst.size_08);

        applyModelRotationAndScale(this.tmdDustModel_800d4d40);
        this.renderSmapModel(this.tmdDustModel_800d4d40);

        this.tmdDustModel_800d4d40.remainingFrames_9e = 0;
        this.tmdDustModel_800d4d40.interpolationFrameIndex = 0;

        this.tmdDustModel_800d4d40.modelParts_00[0].coord2_04.flg--;
        inst.tick_00++;

        prev = inst;
        inst = inst.next_1c;
      }
      //LAB_800ef888
    }
    //LAB_800ef894
  }

  @Method(0x800ef8acL)
  private void renderOrthoQuadTrailEffects() {
    final int[] v = new int[4];
    v[3] = 64; // Other values are 0

    //LAB_800ef9cc
    OrthoTrailParticle54 prev = this.orthoQuadTrail;
    OrthoTrailParticle54 inst = prev.next_50;
    while(inst != null) {
      if(inst.tick_04 >= inst.maxTicks_06 * (3 - vsyncMode_8007a3b8)) {
        prev.next_50 = inst.next_50;
        inst = prev.next_50;
      } else {
        //LAB_800efa08
        final GpuCommandPoly cmd = new GpuCommandPoly(4);

        final int mode = inst.renderMode_00;
        if(mode == 0 || mode == 2) {
          //LAB_800efa44
          final int offsetX = this.screenOffset_800cb568.x - inst.x_18;
          final int offsetY = this.screenOffset_800cb568.y - inst.y_1c;

          cmd
            .pos(0, offsetX + inst.sxy0_20.x, offsetY + inst.sxy0_20.y)
            .pos(1, offsetX + inst.sxy1_28.x, offsetY + inst.sxy1_28.y)
            .pos(2, offsetX + inst.sxy2_30.x, offsetY + inst.sxy2_30.y)
            .pos(3, offsetX + inst.sxy3_38.x, offsetY + inst.sxy3_38.y);

          if(mode == 2) {
            cmd
              .clut(960, 464)
              .vramPos(960, 256)
              .bpp(Bpp.BITS_4)
              .translucent(Translucency.B_MINUS_F);
          } else {
            //LAB_800efb64
            cmd
              .clut((this.cluts_800d6068[7] & 0b111111) * 16, this.cluts_800d6068[7] >>> 6)
              .vramPos((this.texPages_800d6050[7] & 0b1111) * 64, (this.texPages_800d6050[7] & 0b10000) != 0 ? 256 : 0)
              .translucent(Translucency.of(this.texPages_800d6050[7] >>> 5 & 0b11))
              .bpp(Bpp.of(this.texPages_800d6050[7] >>> 7 & 0b11));
          }
        } else if(mode == 1) {
          //LAB_800efb7c
          inst.size_08 += inst.sizeStep_0c / (3 - vsyncMode_8007a3b8);
          inst.sxy0_20.x = inst.z0_26 - inst.size_08 / 2.0f;
          inst.sxy0_20.y = inst.z1_2e - inst.size_08 / 2.0f;
          final float x = this.screenOffset_800cb568.x - inst.x_18 + inst.sxy0_20.x;
          final float y = this.screenOffset_800cb568.y - inst.y_1c + inst.sxy0_20.y;

          cmd
            .pos(0, x, y)
            .pos(1, x + inst.size_08, y)
            .pos(2, x, y + inst.size_08)
            .pos(3, x + inst.size_08, y + inst.size_08);

          if((inst.tick_04 & 0x3) == 0) {
            inst.z1_2e -= 1.0f / (3 - vsyncMode_8007a3b8);
          }

          //LAB_800efc4c
          cmd
            .clut((this.cluts_800d6068[6] & 0b111111) * 16, this.cluts_800d6068[6] >>> 6)
            .vramPos((this.texPages_800d6050[6] & 0b1111) * 64, (this.texPages_800d6050[6] & 0b10000) != 0 ? 256 : 0)
            .translucent(Translucency.of(this.texPages_800d6050[6] >>> 5 & 0b11))
            .bpp(Bpp.of(this.texPages_800d6050[6] >>> 7 & 0b11));
        }

        //LAB_800efc64
        if(inst.tick_04 >= this.particleFadeDelay_800d6c0c[inst.renderMode_00] * (3 - vsyncMode_8007a3b8)) {
          inst.brightness_48 -= inst.stepBrightness_40 / (3 - vsyncMode_8007a3b8);

          if(inst.brightness_48 >= 1.0f || inst.brightness_48 < 0.0f) {
            inst.brightness_48 = 0.0f;
          }
        }

        //LAB_800efcb8
        cmd
          .monochrome(inst.brightness_48)
          .uv(0, this.orthoDustUs_800d6bdc[inst.textureIndex_02], v[inst.textureIndex_02])
          .uv(1, this.orthoDustUs_800d6bdc[inst.textureIndex_02] + this.dustTextureWidths_800d6bec[inst.textureIndex_02], v[inst.textureIndex_02])
          .uv(2, this.orthoDustUs_800d6bdc[inst.textureIndex_02], v[inst.textureIndex_02] + this.dustTextureHeights_800d6bfc[inst.textureIndex_02])
          .uv(3, this.orthoDustUs_800d6bdc[inst.textureIndex_02] + this.dustTextureWidths_800d6bec[inst.textureIndex_02], v[inst.textureIndex_02] + this.dustTextureHeights_800d6bfc[inst.textureIndex_02]);

        GPU.queueCommand(inst.z_4c, cmd);

        inst.tick_04++;
        prev = inst;
        inst = inst.next_50;
      }
      //LAB_800efe48
    }
    //LAB_800efe54
  }

  @Method(0x800f0370L)
  private void initAttachedSobjEffects() {
    initModel(this.tmdDustModel_800d4d40, this.dustTmd, this.dustAnimation);
    this.orthoQuadTrail.next_50 = null;
    this.tmdTrail_800d4ec0.next_1c = null;
    this.initLawPodTrail();
  }

  @Method(0x800f03c0L)
  private TmdTrailParticle20 addTmdDustParticle(final TmdTrailParticle20 parent) {
    final TmdTrailParticle20 child = new TmdTrailParticle20();
    child.next_1c = parent.next_1c;
    parent.next_1c = child;
    return child;
  }

  @Method(0x800f0400L)
  private OrthoTrailParticle54 addOrthoQuadTrailParticle(final OrthoTrailParticle54 parent) {
    final OrthoTrailParticle54 child = new OrthoTrailParticle54();
    child.next_50 = parent.next_50;
    parent.next_50 = child;
    return child;
  }

  @Method(0x800f0440L)
  private void deallocateAttachedSobjEffects() {
    this.deallocateTmdTrail();
    this.deallocateOrthoQuadTrail();
    this.deallocateLawPodTrail();
  }

  @Method(0x800f047cL)
  private void renderAttachedSobjEffects() {
    this.renderTmdTrail();
    this.renderOrthoQuadTrailEffects();
    this.renderLawPodTrail();
  }

  @Method(0x800f04acL)
  private void initAttachedSobjEffectData(final AttachedSobjEffectData40 data) {
    data.tick_00 = 0;
    data.shouldRenderTmdDust_04 = false;
    data.shouldRenderFootprints_08 = false;
    data.shouldRenderOrthoDust_0c = false;
    data.footprintMode_10 = 0;
    data.shouldRenderLawPodTrail_18 = false;
    data.textureIndexType1_1c = 0;
    data.transfer_1e.zero();
    data.size_28 = 0;
    data.oldFootprintInstantiationInterval_2c = 0;
    data.instantiationIntervalDust30 = 0;
    data.instantiationIntervalFootprints_34 = 0;
    data.maxTicks_38 = 0;
    data.trailData_3c = null;
  }

  @Method(0x800f058cL)
  private void deallocateTmdTrail() {
    final TmdTrailParticle20 prev = this.tmdTrail_800d4ec0;

    if(prev.next_1c != null) {
      //LAB_800f05b4
      TmdTrailParticle20 next;
      do {
        final TmdTrailParticle20 inst = prev.next_1c;
        next = inst.next_1c;
        prev.next_1c = next;
      } while(next != null);
    }

    //LAB_800f05d4
  }

  @Method(0x800f05e8L)
  private void deallocateOrthoQuadTrail() {
    final OrthoTrailParticle54 prev = this.orthoQuadTrail;

    if(prev.next_50 != null) {
      //LAB_800f0610
      OrthoTrailParticle54 next;
      do {
        final OrthoTrailParticle54 inst = prev.next_50;
        next = inst.next_50;
        prev.next_50 = next;
      } while(next != null);
    }
    //LAB_800f0630
  }

  @Method(0x800f0644L)
  private void tickLawPodTrail(final Model124 model, final AttachedSobjEffectData40 data) {
    if((data.tick_00 & 0x1) == 0) {
      final LawPodTrailData18 trailData = data.trailData_3c;

      if(trailData.countSegments_01 < trailData.maxCountSegments_00) {
        final LawPodTrailSegment34 prevSegment = this.lawPodTrail_800d4f90;
        final LawPodTrailSegment34 segment = new LawPodTrailSegment34();
        segment.next_30 = prevSegment.next_30;
        prevSegment.next_30 = segment;

        final TrailSegmentVertices14 verts = this.lawPodTrailVerts_800d4fd0;
        TrailSegmentVertices14 newVerts = new TrailSegmentVertices14();
        newVerts.next_10 = verts.next_10;
        verts.next_10 = newVerts;

        final MV transforms = new MV();
        GsGetLs(model.coord2_14, transforms);

        PushMatrix();
        GTE.setTransforms(transforms);
        GTE.perspectiveTransform(-trailData.width_08, 0.0f, 0.0f);
        newVerts.vert0_00.x = GTE.getScreenX(2);
        newVerts.vert0_00.y = GTE.getScreenY(2);
        segment.z_20 = GTE.getScreenZ(3) / 4.0f;

        GTE.perspectiveTransform(trailData.width_08, 0.0f, 0.0f);
        newVerts.vert1_08.x = GTE.getScreenX(2);
        newVerts.vert1_08.y = GTE.getScreenY(2);
        segment.z_20 = GTE.getScreenZ(3) / 4.0f;
        PopMatrix();

        segment.tick_00 = 0;
        segment.tpage_04 = GetTPage(Bpp.BITS_4, Translucency.of(trailData.translucency_0c), 972, 320);
        this.initLawPodTrailSegmentColour(trailData, segment);
        newVerts.vert0_00.x -= this.screenOffset_800cb568.x;
        newVerts.vert0_00.y -= this.screenOffset_800cb568.y;
        newVerts.vert1_08.x -= this.screenOffset_800cb568.x;
        newVerts.vert1_08.y -= this.screenOffset_800cb568.y;

        if(trailData.countSegments_01 == 0) {
          trailData.currSegmentOriginVerts_14 = newVerts;
          newVerts = new TrailSegmentVertices14();
          newVerts.next_10 = verts.next_10;
          verts.next_10 = newVerts;
          final TrailSegmentVertices14 tempVerts = trailData.currSegmentOriginVerts_14;
          newVerts.vert0_00.set(tempVerts.vert0_00);
          newVerts.vert1_08.set(tempVerts.vert1_08);
        }

        //LAB_800f0928
        segment.endpointVerts23_28 = newVerts;
        segment.originVerts01_24 = trailData.currSegmentOriginVerts_14;
        trailData.currSegmentOriginVerts_14 = newVerts;
        segment.trailData_2c = trailData;
        trailData.countSegments_01++;
      }
    }
    //LAB_800f094c
  }

  @Method(0x800f0970L)
  private void renderLawPodTrail() {
    TrailSegmentVertices14 childVerts;
    LawPodTrailSegment34 prevSegment = this.lawPodTrail_800d4f90;
    LawPodTrailSegment34 segment = prevSegment.next_30;

    //LAB_800f09c0
    while(segment != null) {
      final LawPodTrailData18 trailData = segment.trailData_2c;
      if(segment.tick_00 <= trailData.maxTicks_06) {
        final int tpage = segment.tpage_04;

        //LAB_800f0b04
        final GpuCommandPoly cmd = new GpuCommandPoly(4)
          .translucent(Translucency.of(tpage >>> 5 & 0b11))
          .pos(0, this.screenOffset_800cb568.x + segment.originVerts01_24.vert0_00.x, this.screenOffset_800cb568.y + segment.originVerts01_24.vert0_00.y)
          .pos(1, this.screenOffset_800cb568.x + segment.originVerts01_24.vert1_08.x, this.screenOffset_800cb568.y + segment.originVerts01_24.vert1_08.y)
          .pos(2, this.screenOffset_800cb568.x + segment.endpointVerts23_28.vert0_00.x, this.screenOffset_800cb568.y + segment.endpointVerts23_28.vert0_00.y)
          .pos(3, this.screenOffset_800cb568.x + segment.endpointVerts23_28.vert1_08.x, this.screenOffset_800cb568.y + segment.endpointVerts23_28.vert1_08.y);

        final int r;
        final int g;
        final int b;
        if(segment.tick_00 <= trailData.colourTickDelay_02 - 1) {
          //LAB_800f0d0c
          r = segment.rAccumulator_14 >> 16;
          g = segment.gAccumulator_18 >> 16;
          b = segment.bAccumulator_1c >> 16;
        } else {
          segment.rAccumulator_14 -= segment.rStep_08;
          segment.gAccumulator_18 -= segment.gStep_0c;
          segment.bAccumulator_1c -= segment.bStep_10;
          r = Math.max(0, segment.rAccumulator_14 >> 16);
          g = Math.max(0, segment.gAccumulator_18 >> 16);
          b = Math.max(0, segment.bAccumulator_1c >> 16);
        }

        //LAB_800f0d18
        cmd.rgb(0, r, g, b);
        cmd.rgb(1, r, g, b);
        cmd.rgb(2, b, g, r);
        cmd.rgb(3, b, g, r);
        GPU.queueCommand(segment.z_20, cmd);

        prevSegment = segment;
        segment.tick_00++;
        segment = segment.next_30;
      } else {
        trailData.countSegments_01--;
        childVerts = this.lawPodTrailVerts_800d4fd0.next_10;
        TrailSegmentVertices14 currSegmentOriginVerts = segment.originVerts01_24;
        TrailSegmentVertices14 parentVerts = this.lawPodTrailVerts_800d4fd0;

        //LAB_800f09fc
        while(childVerts != null) {
          if(childVerts == currSegmentOriginVerts) {
            //LAB_800f0ae8
            parentVerts.next_10 = childVerts.next_10;
            break;
          }

          parentVerts = childVerts;
          childVerts = childVerts.next_10;
        }

        //LAB_800f0a18
        if(trailData.countSegments_01 == 0) {
          childVerts = this.lawPodTrailVerts_800d4fd0.next_10;
          currSegmentOriginVerts = segment.endpointVerts23_28;
          parentVerts = this.lawPodTrailVerts_800d4fd0;

          //LAB_800f0a38
          while(childVerts != null) {
            if(childVerts == currSegmentOriginVerts) {
              //LAB_800f0acc
              currSegmentOriginVerts = childVerts.next_10;
              parentVerts.next_10 = currSegmentOriginVerts;
              break;
            }

            parentVerts = childVerts;
            childVerts = childVerts.next_10;
          }

          //LAB_800f0a54
        }

        //LAB_800f0a58
        prevSegment.next_30 = segment.next_30;

        segment = prevSegment.next_30;
        if(trailData.countSegments_01 == 0) {
          if(this.lawPodTrail_800d4f90.next_30.next_30 == null) {
            //LAB_800f0aa4
            while(this.lawPodTrailVerts_800d4fd0.next_10 != null) {
              childVerts = this.lawPodTrailVerts_800d4fd0.next_10;
              this.lawPodTrailVerts_800d4fd0.next_10 = childVerts.next_10;
            }
          }
        }
      }
      //LAB_800f0d68
    }

    //LAB_800f0d74
    if(this.lawPodTrailSegmentCount_800f9e78 == 0 && this.lawPodTrail_800d4f90.next_30 == null) {
      //LAB_800f0da8
      while(this.lawPodTrailVerts_800d4fd0.next_10 != null) {
        childVerts = this.lawPodTrailVerts_800d4fd0.next_10;
        this.lawPodTrailVerts_800d4fd0.next_10 = childVerts.next_10;
      }
    }
    //LAB_800f0dc8
  }

  @Method(0x800f0df0L)
  private void initLawPodTrailSegmentColour(final LawPodTrailData18 trailData, final LawPodTrailSegment34 segment) {
    segment.rAccumulator_14 = trailData.r_10 << 16;
    segment.gAccumulator_18 = trailData.g_11 << 16;
    segment.bAccumulator_1c = trailData.b_12 << 16;
    segment.rStep_08 = segment.rAccumulator_14 / trailData.countColourSteps_04;
    segment.gStep_0c = segment.gAccumulator_18 / trailData.countColourSteps_04;
    segment.bStep_10 = segment.bAccumulator_1c / trailData.countColourSteps_04;
  }

  @Method(0x800f0e60L)
  private void initLawPodTrail() {
    this.lawPodTrailSegmentCount_800f9e78 = 0;
    this.lawPodTrail_800d4f90.next_30 = null;
    this.lawPodTrailVerts_800d4fd0.next_10 = null;
  }

  @Method(0x800f0e7cL)
  private void deallocateLawPodTrail() {
    final LawPodTrailSegment34 parentSegment = this.lawPodTrail_800d4f90;
    if(parentSegment.next_30 != null) {
      //LAB_800f0ea4
      LawPodTrailSegment34 tempSegment;
      do {
        final LawPodTrailSegment34 childSegment = parentSegment.next_30;
        tempSegment = childSegment.next_30;
        parentSegment.next_30 = tempSegment;
      } while(tempSegment != null);
    }

    //LAB_800f0ec8
    final TrailSegmentVertices14 parentVerts = this.lawPodTrailVerts_800d4fd0;

    if(parentVerts.next_10 != null) {
      //LAB_800f0edc
      TrailSegmentVertices14 tempVerts;
      do {
        final TrailSegmentVertices14 childVerts = parentVerts.next_10;
        tempVerts = childVerts.next_10;
        parentVerts.next_10 = tempVerts;
      } while(tempVerts != null);
    }

    //LAB_800f0efc
    this.clearLawPodTrailSegmentsList();
    this.lawPodTrailSegmentCount_800f9e78 = 0;
  }

  @Method(0x800f0f20L)
  private LawPodTrailData18 addLawPodTrailSegment() {
    LawPodTrailData18 segment = null;

    //LAB_800f0f3c
    for(int i = 0; i < 8; i++) {
      if(this.lawPodTrailSegments_800f9e7c[i] == null) {
        segment = new LawPodTrailData18();
        this.lawPodTrailSegments_800f9e7c[i] = segment;
        break;
      }
    }

    //LAB_800f0f78
    return segment;
  }

  @Method(0x800f0fe8L)
  private void clearLawPodTrailSegmentsList() {
    for(int i = 0; i < 8; i++) {
      if(this.lawPodTrailSegments_800f9e7c[i] != null) {
        this.lawPodTrailSegments_800f9e7c[i] = null;
        this.lawPodTrailSegmentCount_800f9e78--;
      }
    }
  }

  @ScriptDescription("Allocates/initializes static struct containing smoke plume particle data.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT_ARRAY, name = "smokeData", description = "An array of data for the smoke plume particle data struct")
  @Method(0x800f1060L)
  private FlowControl scriptAllocateSmokePlumeEffectData(final RunningScript<?> script) {
    this.smokePlumeEffect_800d5fd8.allocateSmokePlumeEffect(script, this.screenOffset_800cb568.x, this.screenOffset_800cb568.y, this.texPages_800d6050[9], this.cluts_800d6068[9]);

    //LAB_800f1250
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Allocates/initializes struct data for unused smoke effect.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "instantiationTicks", description = "Number of ticks before a new particle is instantiated.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "lifecycleTicks", description = "Number of ticks an individual particle exists for.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "delayTicks", description = "Number of ticks to delay beginning particle instantiation.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "X-coordinate of particle.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "Y-coordinate of particle.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "Z-coordinate of particle.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "totalDistance", description = "Distance particle instance travels over lifecycle.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "offsetRandomX", description = "Multiplication factor of randomized component of particle x-offset.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "size", description = "Size of the particle.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sizeChange", description = "Amount the size of the particle changes over lifecycle.")
  @Method(0x800f1274L)
  private FlowControl scriptAllocateUnusedSmokeEffectData(final RunningScript<?> script) {
    this.smokeCloudEffect_800d4f50.allocateUnusedSmokeEffect(script, this.screenOffset_800cb568.x, this.screenOffset_800cb568.y, this.texPages_800d6050[6], this.cluts_800d6068[6]);

    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Allocates/initializes struct storing data for smoke cloud particle effect.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "effectState")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "instantiationTicks", description = "Number of ticks before a new particle is instantiated.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "lifecycleTicks", description = "Number of ticks an individual particle exists for.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "offsetBaseX", description = "Static base x-offset of particle instance.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "offsetY", description = "Y-offset of particle instance.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "offsetRandomX", description = "Multiplication factor of randomized component of particle x-offset.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "totalDistance", description = "Distance particle instance travels over lifecycle.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "size", description = "Size of the particle instance.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sizeChange", description = "Amount the size of the particle instance changes over lifecycle.")
  @Method(0x800f14f0L)
  private FlowControl scriptAllocateSmokeCloudData(final RunningScript<?> script) {
    this.smokeCloudEffect_800d4f50.allocateSmokeCloudEffect(script, this.texPages_800d6050[6], this.cluts_800d6068[6]);

    //LAB_800f162c
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Initializes the trail effect on the law pods in Zenebatos.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "p0")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "p1")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "p2")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "p3")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "width")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "translucency")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "r")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "g")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "b")
  @Method(0x800f1634L)
  private FlowControl scriptInitLawPodTrail(final RunningScript<?> script) {
    final ScriptState<?> state = script.scriptState_04;

    script.params_20[9] = new ScriptStorageParam(state, 0);

    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[state.storage_44[0]].innerStruct_00;
    if(script.params_20[0].get() == 0 || this.lawPodTrailSegmentCount_800f9e78 >= 8) {
      //LAB_800f1698
      sobj.attachedEffectData_1d0.shouldRenderLawPodTrail_18 = false;
    } else {
      //LAB_800f16a4
      final LawPodTrailData18 segment = this.addLawPodTrailSegment();
      segment.maxCountSegments_00 = script.params_20[1].get();
      segment.countSegments_01 = 0;
      segment.colourTickDelay_02 = script.params_20[2].get();
      segment.countColourSteps_04 = script.params_20[3].get();
      segment.maxTicks_06 = segment.colourTickDelay_02 + segment.countColourSteps_04;
      segment.width_08 = script.params_20[4].get();
      segment.translucency_0c = script.params_20[5].get();
      segment.r_10 = script.params_20[6].get();
      segment.g_11 = script.params_20[7].get();
      segment.b_12 = script.params_20[8].get();
      segment.currSegmentOriginVerts_14 = null;
      sobj.attachedEffectData_1d0.shouldRenderLawPodTrail_18 = script.params_20[0].get() == 1;
      sobj.attachedEffectData_1d0.trailData_3c = segment;
      this.lawPodTrailSegmentCount_800f9e78++;
    }

    //LAB_800f1784
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Adds a save point to the map")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.BOOL, name = "hasSavePoint", description = "True to add a savepoint, false otherwise")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x", description = "The X position")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y", description = "The Y position")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "z", description = "The Z position")
  @Method(0x800f179cL)
  private FlowControl scriptAddSavePoint(final RunningScript<?> script) {
    final Vector2f sp0x48 = new Vector2f();
    final Vector2f sp0x50 = new Vector2f();
    final GsCOORDINATE2 coord2 = new GsCOORDINATE2();

    this.hasSavePoint_800d5620 = script.params_20[0].get() != 0;
    GsInitCoordinate2(null, coord2);

    coord2.coord.transfer.set(script.params_20[1].get(), script.params_20[2].get(), script.params_20[3].get());
    this.savePointPos_800d5622.set(coord2.coord.transfer);

    final MV screenMatrix = new MV();
    GsGetLs(coord2, screenMatrix);
    PushMatrix();

    GTE.setTransforms(screenMatrix);

    //LAB_800f195c
    for(int s3 = 0; s3 < 2; s3++) {
      final SavePointRenderData44 struct = this.savePoint_800d5598[s3];

      struct.z_40 = RotTransPers4(this.savePointV0_800d6c28, this.savePointV1_800d6c30, this.savePointV2_800d6c38, this.savePointV3_800d6c40, struct.vert0_00, struct.vert1_08, struct.vert2_10, struct.vert3_18);

      if(s3 == 0) {
        perspectiveTransform(this._800d6c48, sp0x48);
        perspectiveTransform(this._800d6c50, sp0x50);

        sp0x48.x = sp0x50.y - sp0x48.y;
      }

      //LAB_800f1a34
      final float a3 = (struct.vert0_00.x + struct.vert3_18.x) / 2.0f;
      final float t0 = (struct.vert0_00.y + struct.vert3_18.y) / 2.0f;
      final float a2 = (struct.vert0_00.x - struct.vert3_18.x) / 2.0f;

      final float x0 = a3 - a2;
      struct.vert0_00.x = x0;
      struct.vert2_10.x = x0;
      final float y0 = t0 - a2 - sp0x48.x;
      struct.vert0_00.y = y0;
      struct.vert1_08.y = y0;
      final float x1 = a3 + a2;
      struct.vert1_08.x = x1;
      struct.vert3_18.x = x1;
      final float y1 = t0 + a2 - sp0x48.x;
      struct.vert2_10.y = y1;
      struct.vert3_18.y = y1;

      //LAB_800f1b04
      struct.screenOffsetX_20 = this.screenOffset_800cb568.x;
      struct.screenOffsetY_24 = this.screenOffset_800cb568.y;
    }

    PopMatrix();

    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Unknown, adds triangle indicators (possibly for doors)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT_ARRAY, name = "data", description = "The struct data")
  @Method(0x800f1b64L)
  private FlowControl FUN_800f1b64(final RunningScript<?> script) {
    final Vector3f sp0x10 = new Vector3f();
    final GsCOORDINATE2 sp0x18 = new GsCOORDINATE2();
    final MV sp0x70 = new MV();

    GsInitCoordinate2(null, sp0x18);

    final TriangleIndicator140 indicator = this.triangleIndicator_800c69fc;

    //LAB_800f1ba8
    final Param ints = script.params_20[0];
    int s0 = 0;
    for(int i = 0; ints.array(s0).get() != -1; i++) {
      this.collisionGeometry_800cbe08.getMiddleOfCollisionPrimitive(ints.array(s0++).get(), sp0x10);

      sp0x18.coord.transfer.set(sp0x10);
      GsGetLs(sp0x18, sp0x70);

      PushMatrix();
      GTE.setTransforms(sp0x70);
      GTE.perspectiveTransform(0, 0, 0);
      final float sx = GTE.getScreenX(2);
      final float sy = GTE.getScreenY(2);
      PopMatrix();

      indicator._18[i] = (short)ints.array(s0++).get();
      indicator.x_40[i] = sx + ints.array(s0++).get();
      indicator.y_68[i] = sy + ints.array(s0++).get();
      indicator.screenOffsetX_90[i] = this.screenOffset_800cb568.x;
      indicator.screenOffsetY_e0[i] = this.screenOffset_800cb568.y;
    }

    //LAB_800f1cf0
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Unknown, adds a triangle indicator (possibly for a door)")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "p1")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "x")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "y")
  @Method(0x800f1d0cL)
  private FlowControl FUN_800f1d0c(final RunningScript<?> script) {
    final GsCOORDINATE2 sp0x40 = new GsCOORDINATE2();
    GsInitCoordinate2(null, sp0x40);
    final Vector3f sp0x10 = new Vector3f();
    this.collisionGeometry_800cbe08.getMiddleOfCollisionPrimitive(script.params_20[0].get(), sp0x10);
    sp0x40.coord.transfer.set(sp0x10);
    final MV sp0x20 = new MV();
    GsGetLs(sp0x40, sp0x20);

    PushMatrix();
    GTE.setTransforms(sp0x20);
    GTE.perspectiveTransform(0, 0, 0);
    final float sx = GTE.getScreenX(2);
    final float sy = GTE.getScreenY(2);
    PopMatrix();

    //LAB_800f1e20
    for(int i = 0; i < 20; i++) {
      final TriangleIndicator140 indicator = this.triangleIndicator_800c69fc;

      if(indicator._18[i] == -1) {
        indicator._18[i] = (short)script.params_20[1].get();
        indicator.x_40[i] = sx + script.params_20[2].get();
        indicator.y_68[i] = sy + script.params_20[3].get();
        indicator.screenOffsetX_90[i] = this.screenOffset_800cb568.x;
        indicator.screenOffsetY_e0[i] = this.screenOffset_800cb568.y;
        break;
      }
    }

    //LAB_800f1ea0
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "p0")
  @Method(0x800f1eb8L)
  private FlowControl FUN_800f1eb8(final RunningScript<?> script) {
    throw new RuntimeException("Not implemented");
  }

  @ScriptDescription("Initializes TMD dust particle attached sobj effect.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.BOOL, name = "shouldRender", description = "Whether effect should be rendered.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "size", description = "Maximum size of particles.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "instantiationTicks", description = "New number of ticks before a new particle is instantiated.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "maxTicks", description = "Number of ticks for which a particle exists.")
  @Method(0x800f1f9cL)
  private FlowControl scriptInitTmdDust(final RunningScript<?> script) {
    final SubmapObject210 sobj = SCRIPTS.getObject(script.params_20[0].get(), SubmapObject210.class);
    sobj.attachedEffectData_1d0.shouldRenderTmdDust_04 = script.params_20[1].get() == 1;
    sobj.attachedEffectData_1d0.size_28 = script.params_20[2].get();
    sobj.attachedEffectData_1d0.instantiationIntervalDust30 = script.params_20[3].get();

    if(script.params_20[4].get() == 0) {
      sobj.attachedEffectData_1d0.maxTicks_38 = 1;
    } else {
      sobj.attachedEffectData_1d0.maxTicks_38 = script.params_20[4].get();
    }

    //LAB_800f2018
    if(!sobj.attachedEffectData_1d0.shouldRenderTmdDust_04) {
      sobj.attachedEffectData_1d0.transfer_1e.zero();
      sobj.attachedEffectData_1d0.size_28 = 1;
      sobj.attachedEffectData_1d0.instantiationIntervalDust30 = 0;
      sobj.attachedEffectData_1d0.maxTicks_38 = 0;
    }

    //LAB_800f2040
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Self version of scriptInitTmdDust.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "shouldRender", description = "Whether effect should be rendered.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "size", description = "Maximum size of particles.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "instantiationTicks", description = "New number of ticks before a new particle is instantiated.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "maxTicks", description = "Number of ticks for which a particle exists.")
  @Method(0x800f2048L)
  private FlowControl scriptSelfInitTmdDust(final RunningScript<?> script) {
    script.params_20[4] = script.params_20[3];
    script.params_20[3] = script.params_20[2];
    script.params_20[2] = script.params_20[1];
    script.params_20[1] = script.params_20[0];
    script.params_20[0] = new ScriptStorageParam(script.scriptState_04, 0);
    return this.scriptInitTmdDust(script);
  }

  /** Re-initializes some values for Kadessa steam vents to be intermittent when Divine Dragon flies by. */
  @ScriptDescription("Re-initializes some values for smoke particles.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT_ARRAY, name = "smokeData", description = "An array of data for the smoke plume particle data struct")
  @Method(0x800f2090L)
  private FlowControl scriptReinitializeSmokePlumeForIntermittentBursts(final RunningScript<?> script) {
    this.smokePlumeEffect_800d5fd8.reinitializeSmokePlumeForIntermittentBursts(script);

    //LAB_800f2164
    //LAB_800f2170
    //LAB_800f218c
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Initializes fields for snow particle data.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "unused")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "stepAngleMax", description = "Maximum value for random angle step.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "translationScaleX", description = "Magnitude of additional x step based on the angle of the particle's trajectory.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "stepXMax", description = "Maximum value of random component used in calculating a particle's x step.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "stepYDivisor", description = "Value used in calculated the size of the y step.")
  @Method(0x800f2198L)
  private FlowControl scriptInitSnowParticleData(final RunningScript<?> script) {
    this.snowState_800f9e60 = script.params_20[0].get();

    //LAB_800f21d0
    if(this.snowState_800f9e60 == 1) {
      if(script.params_20[2].get() < 0) {
        script.params_20[2].neg();
      }

      //LAB_800f2210
      this.snow_800d4bd8 = new SnowEffect(psxDegToRad(script.params_20[4].get()), script.params_20[3].get(), script.params_20[1].get(), script.params_20[2].get());
      this.snow_800d4bd8.initSnowEffect();
    }

    //LAB_800f2250
    //LAB_800f225c
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Initializes footprints attached sobj effect when param 0 is 1 or 2.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "shouldRender", description = "Whether effect should initialize/render (occurs on 1 or 2).")
  @Method(0x800f2264L)
  private FlowControl scriptInitFootprints(final RunningScript<?> script) {
    final ScriptState<?> sobj1 = script.scriptState_04;
    script.params_20[1] = new ScriptStorageParam(sobj1, 0); // Unused? Why?

    final SubmapObject210 sobj2 = (SubmapObject210)scriptStatePtrArr_800bc1c0[sobj1.storage_44[0]].innerStruct_00; // Storage 0 is my script index, isn't this just getting the same state?
    if((script.params_20[0].get() == 1 || script.params_20[0].get() == 2)) {
      sobj2.attachedEffectData_1d0.shouldRenderFootprints_08 = true;
      sobj2.attachedEffectData_1d0.footprintMode_10 = 0;
      sobj2.attachedEffectData_1d0.instantiationIntervalFootprints_34 = 9;
    } else {
      //LAB_800f22b8
      sobj2.attachedEffectData_1d0.shouldRenderFootprints_08 = false;
    }

    //LAB_800f22bc
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Initializes footprint attached sobj effect. 0 = individual, 1 = skid")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "footprintMode", description = "Style of footprint (individual or skid).")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "instantiationTicks", description = "New number of ticks before a new particle is instantiated.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "sobjIndex", description = "The SubmapObject210 script index")
  @Method(0x800f22c4L)
  private FlowControl scriptChangeFootprintsMode(final RunningScript<?> script) {
    script.params_20[2] = new ScriptStorageParam(script.scriptState_04, 0);
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.params_20[2].get()].innerStruct_00;

    final int footprintMode = script.params_20[0].get();
    sobj.attachedEffectData_1d0.footprintMode_10 = footprintMode;
    if(footprintMode == 0) {
      final int newInterval = script.params_20[1].get();

      if(newInterval == 0) {
        sobj.attachedEffectData_1d0.shouldRenderFootprints_08 = false;
        sobj.attachedEffectData_1d0.instantiationIntervalFootprints_34 = 0;
        //LAB_800f2328
      } else if(newInterval == 1) {
        sobj.attachedEffectData_1d0.shouldRenderFootprints_08 = true;
        sobj.attachedEffectData_1d0.instantiationIntervalFootprints_34 = sobj.attachedEffectData_1d0.oldFootprintInstantiationInterval_2c;
      }
      //LAB_800f2340
    } else if(footprintMode == 1) {
      sobj.attachedEffectData_1d0.shouldRenderFootprints_08 = true;
      sobj.attachedEffectData_1d0.oldFootprintInstantiationInterval_2c = sobj.attachedEffectData_1d0.instantiationIntervalFootprints_34;
      sobj.attachedEffectData_1d0.instantiationIntervalFootprints_34 = script.params_20[1].get();

      if(sobj.attachedEffectData_1d0.instantiationIntervalFootprints_34 == 0) {
        sobj.attachedEffectData_1d0.instantiationIntervalFootprints_34 = 1;
      }
    }

    //LAB_800f2374
    if(sobj.attachedEffectData_1d0.footprintMode_10 >= 2) {
      sobj.attachedEffectData_1d0.shouldRenderFootprints_08 = false;
      sobj.attachedEffectData_1d0.transfer_1e.zero();
    }

    //LAB_800f2398
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Sets the frequency of footprint attached sobj effect particle instantiation.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "instantiationInterval", description = "Frequency of instantiating new particles.")
  @Method(0x800f23a0L)
  private FlowControl scriptSetFootprintsInstantiationInterval(final RunningScript<?> script) {
    script.params_20[1] = new ScriptStorageParam(script.scriptState_04, 0);

    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.scriptState_04.storage_44[0]].innerStruct_00;
    sobj.attachedEffectData_1d0.instantiationIntervalFootprints_34 = Math.max(1, script.params_20[0].get());

    //LAB_800f23e4
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Initializes ortho quad dust attached sobj effect if mode is 1 or 3.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "mode", description = "Determines whether effect is initialized.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "size", description = "Size of the particles.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "instantiationTicks", description = "Number of ticks before a new particle is instantiated.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "maxTicks", description = "Number of ticks for which particle exists.")
  @Method(0x800f23ecL)
  private FlowControl scriptInitOrthoDust(final RunningScript<?> script) {
    script.params_20[4] = new ScriptStorageParam(script.scriptState_04, 0); // Does nothing, why?
    final int mode = script.params_20[0].get();
    final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[script.scriptState_04.storage_44[0]].innerStruct_00;

    if(mode == 1 || mode == 3) {
      //LAB_800f2430
      sobj.attachedEffectData_1d0.shouldRenderOrthoDust_0c = true;
      sobj.attachedEffectData_1d0.size_28 = script.params_20[1].get();
      sobj.attachedEffectData_1d0.instantiationIntervalDust30 = script.params_20[2].get();
      sobj.attachedEffectData_1d0.maxTicks_38 = Math.max(1, script.params_20[3].get());
    } else {
      //LAB_800f2484
      sobj.attachedEffectData_1d0.shouldRenderOrthoDust_0c = false;
    }

    //LAB_800f2488
    if(!sobj.attachedEffectData_1d0.shouldRenderOrthoDust_0c) {
      sobj.attachedEffectData_1d0.transfer_1e.zero();
    }

    //LAB_800f24a8
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Sets smoke cloud effect to stop ticking.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "mode", description = "Sets whether smoke cloud state should be set to stop effect ticking.")
  @Method(0x800f24b0L)
  private FlowControl scriptSetSmokeCloudEffectStateToDontTick(final RunningScript<?> script) {
    if(script.params_20[0].get() == 1) {
      this.smokeCloudEffect_800d4f50.smokeCloudState = SmokeParticleEffect.SmokeCloudState.DONT_TICK;
    }

    //LAB_800f24d0
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Deallocates/uninitializes the smoke cloud data and effect structs.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "forceDeallocation", description = "Ensures that structs will be deallocated regardless of _800fe70's value.")
  @Method(0x800f24d8L)
  private FlowControl scriptDeallocateSmokeCloudDataAndEffect(final RunningScript<?> script) {
    if(script.params_20[0].get() != 0) {
      this.smokeCloudEffect_800d4f50.smokeCloudState = SmokeParticleEffect.SmokeCloudState.UNINITIALIZED;
    }

    //LAB_800f24fc
    if(this.smokeCloudEffect_800d4f50.smokeCloudState == SmokeParticleEffect.SmokeCloudState.UNINITIALIZED) {
      this.smokeCloudEffect_800d4f50.deallocate();
    }

    //LAB_800f2544
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "p0")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "p1")
  @Method(0x800f2554L)
  private FlowControl FUN_800f2554(final RunningScript<?> script) {
    throw new RuntimeException("Not implemented");
  }

  @ScriptDescription("If param 0 is 1, deallocate the Zenebatos law pod trail effect.")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.BOOL, name = "shouldDeallocate", description = "Deallocate trail effect when true.")
  @Method(0x800f25a8L)
  private FlowControl scriptDeallocateLawPodTrail(final RunningScript<?> script) {
    final ScriptState<?> state = script.scriptState_04;
    script.params_20[1] = new ScriptStorageParam(state, 0);

    if(script.params_20[0].get() == 1) {
      this.deallocateLawPodTrail();
      final SubmapObject210 sobj = (SubmapObject210)scriptStatePtrArr_800bc1c0[state.storage_44[0]].innerStruct_00;
      sobj.attachedEffectData_1d0.shouldRenderLawPodTrail_18 = false;
    }

    //LAB_800f2604
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Allocates triangle indicators from an array of struct data")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT_ARRAY, name = "data", description = "The struct data")
  @Method(0x800f2618L)
  private FlowControl scriptAllocateTriangleIndicatorArray(final RunningScript<?> script) {
    final TriangleIndicator140 indicator = this.triangleIndicator_800c69fc;

    //LAB_800f266c
    int i = 0;
    final Param a0 = script.params_20[0];
    for(int a1 = 0; a0.array(i).get() != -1; a1++) {
      indicator._18[a1] = (short)a0.array(i++).get();
      indicator.x_40[a1] = a0.array(i++).get() + this.screenOffset_800cb568.x;
      indicator.y_68[a1] = a0.array(i++).get() + this.screenOffset_800cb568.y;
      indicator.screenOffsetX_90[a1] = this.screenOffset_800cb568.x;
      indicator.screenOffsetY_e0[a1] = this.screenOffset_800cb568.y;
    }

    //LAB_800f26b4
    return FlowControl.CONTINUE;
  }

  @ScriptDescription("Unknown")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "p0")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "p1")
  @ScriptParam(direction = ScriptParam.Direction.IN, type = ScriptParam.Type.INT, name = "p2")
  @Method(0x800f26c8L)
  private FlowControl FUN_800f26c8(final RunningScript<?> script) {
    throw new RuntimeException("Not implemented");
  }

  @ScriptDescription("No-op")
  @Method(0x800f2780L)
  private FlowControl FUN_800f2780(final RunningScript<?> script) {
    return FlowControl.CONTINUE;
  }

  @Method(0x800f2788L)
  private void initSavePoint() {
    initModel(this.savePointModel_800d5eb0, this.savepointTmd, this.savepointAnimation);
    this.savePoint_800d5598[0].rotation_28 = 0.0f;
    this.savePoint_800d5598[0].colour_34 = 0.3125f;
    this.savePoint_800d5598[1].rotation_28 = 0.0f;
    this.savePoint_800d5598[1].fadeAmount_2c = 0.0077f;
    this.savePoint_800d5598[1].colour_34 = 0.0f;
    this.savePoint_800d5598[1].fadeState_38 = 0;

    //LAB_800f285c
    for(int i = 0; i < 8; i++) {
      final SavePointRenderData44 struct0 = this.savePoint_800d5630[i * 4];
      final SavePointRenderData44 struct1 = this.savePoint_800d5630[i * 4 + 1];
      final SavePointRenderData44 struct2 = this.savePoint_800d5630[i * 4 + 2];
      final SavePointRenderData44 struct3 = this.savePoint_800d5630[i * 4 + 3];
      struct0.colour_34 = 0.5f;
      struct0.fadeAmount_2c = 0.0078f;
      struct0.fadeState_38 = 0;
      struct0.rotation_28 = MathHelper.psxDegToRad(this._800d6c58[i]);
      struct1.colour_34 = 0.375f;
      struct2.colour_34 = struct0.colour_34 - 0.25f;
      struct3.colour_34 = struct0.colour_34 - 0.375f;
    }
  }

  @Method(0x800f28d8L)
  private void renderSavePoint() {
    float minX = 0.0f;
    float maxX = 0.0f;
    float minY = 0.0f;
    float maxY = 0.0f;

    final Model124 model = this.savePointModel_800d5eb0;
    model.coord2_14.transforms.scale.set(1.5f, 3.0f, 1.5f);
    model.coord2_14.coord.transfer.set(this.savePointPos_800d5622);

    applyModelRotationAndScale(model);
    final int interpolationFrameCount = (2 - vsyncMode_8007a3b8) * 2 + 1;
    animateModel(model, interpolationFrameCount);
    this.renderSmapModel(model);

    GPU.queueCommand(1, new GpuCommandCopyVramToVram(984, 288 + this._800f9ea0, 992, 288, 8, 64 - this._800f9ea0));
    GPU.queueCommand(1, new GpuCommandCopyVramToVram(984, 288, 992, 352 - this._800f9ea0, 8, this._800f9ea0));

    this._800f9ea0 = this._800f9ea0 + 1 & 0x3f;

    //LAB_800f2a44
    // This loop renders the central circle
    for(int i = 0; i < 2; i++) {
      final SavePointRenderData44 s0 = this.savePoint_800d5598[i];

      final int offsetX = this.screenOffset_800cb568.x - s0.screenOffsetX_20;
      final int offsetY = this.screenOffset_800cb568.y - s0.screenOffsetY_24;

      final float x0 = offsetX + s0.vert0_00.x;
      final float y0 = offsetY + s0.vert0_00.y;
      final float x1 = offsetX + s0.vert1_08.x;
      final float y1 = offsetY + s0.vert1_08.y;
      final float x2 = offsetX + s0.vert2_10.x;
      final float y2 = offsetY + s0.vert2_10.y;
      final float x3 = offsetX + s0.vert3_18.x;
      final float y3 = offsetY + s0.vert3_18.y;

      if(i == 0) {
        minX = x0;
        minY = y0;
        maxX = x3;
        maxY = y3;
      }

      //LAB_800f2af8
      if(i == 1) {
        if(s0.fadeState_38 == 0) {
          //LAB_800f2b44
          s0.colour_34 += s0.fadeAmount_2c;

          if(s0.colour_34 > 0.5f) {
            s0.colour_34 = 0.5f;
            s0.fadeState_38 = 1;
          }
        } else {
          s0.colour_34 -= s0.fadeAmount_2c;

          if(s0.colour_34 < 0.0f) {
            s0.colour_34 = 0.0f;
            s0.fadeState_38 = 0;
          }
        }
      }

      //LAB_800f2b80
      if(s0.z_40 == 0) {
        s0.z_40++;
      }

      GPU.queueCommand(s0.z_40, new GpuCommandPoly(4)
        .bpp(Bpp.of(this.texPages_800d6050[5] >>> 7 * 0b11))
        .translucent(Translucency.B_PLUS_F)
        .monochrome((int)(s0.colour_34 * 255.0f))
        .clut((this.cluts_800d6068[5] & 0b111111) * 16, this.cluts_800d6068[5] >>> 6)
        .vramPos((this.texPages_800d6050[5] & 0b1111) * 64, (this.texPages_800d6050[5] & 0b10000) != 0 ? 256 : 0)
        .pos(0, x0, y0)
        .uv(0, 160, 64)
        .pos(1, x1, y1)
        .uv(1, 191, 64)
        .pos(2, x2, y2)
        .uv(2, 160, 95)
        .pos(3, x3, y3)
        .uv(3, 191, 95)
      );
    }

    final float sp80 = (minX - maxX) / 2.0f;
    final float sp68 = (minX + maxX) / 2.0f;
    final float sp78 = (maxY - minY) / 2.0f;
    final float sp6a = (maxY + minY) / 2.0f;

    //LAB_800f2de8
    for(int fp = 0; fp < 8; fp++) {
      final SavePointRenderData44 struct0 = this.savePoint_800d5630[fp * 4];
      final SavePointRenderData44 struct1 = this.savePoint_800d5630[fp * 4 + 1];
      final SavePointRenderData44 struct2 = this.savePoint_800d5630[fp * 4 + 2];
      final SavePointRenderData44 struct3 = this.savePoint_800d5630[fp * 4 + 3];
      struct3.vert0_00.x = struct2.vert0_00.x;
      struct3.vert0_00.y = struct2.vert0_00.y;
      struct2.vert0_00.x = struct1.vert0_00.x;
      struct2.vert0_00.y = struct1.vert0_00.y;
      struct1.vert0_00.x = struct0.vert0_00.x;
      struct1.vert0_00.y = struct0.vert0_00.y;
      struct0.vert0_00.x = sp68 + (sp80 + this._800d6c78[fp]) * sin(struct0.rotation_28);
      struct0.vert0_00.y = sp6a + (sp78 + this._800d6c78[fp]) * cos(struct0.rotation_28);

      if(struct0.fadeState_38 != 0) {
        struct0.colour_34 -= struct0.fadeAmount_2c;

        if(struct0.colour_34 < 0.0f) {
          struct0.colour_34 = 0.0f;
          struct0.fadeState_38 = 0;
        }
      } else {
        //LAB_800f2f0c
        struct0.colour_34 += struct0.fadeAmount_2c;

        if(struct0.colour_34 > 0.5f) {
          struct0.colour_34 = 0.5f;
          struct0.fadeState_38 = 1;
        }
      }

      //LAB_800f2f4c
      //LAB_800f2f50
      //LAB_800f2f78
      for(int s4 = 0; s4 < 4; s4++) {
        final SavePointRenderData44 struct = this.savePoint_800d5630[fp + s4];

        final GpuCommandPoly cmd = new GpuCommandPoly(4)
          .bpp(Bpp.of(this.texPages_800d6050[4] >>> 7 * 0b11))
          .translucent(Translucency.B_PLUS_F)
          .monochrome((int)(struct.colour_34 * 255.0f))
          .clut((this.cluts_800d6068[4] & 0b111111) * 16, this.cluts_800d6068[4] >>> 6)
          .vramPos((this.texPages_800d6050[4] & 0b1111) * 64, (this.texPages_800d6050[4] & 0b10000) != 0 ? 256 : 0)
          .pos(0, struct.vert0_00.x, struct.vert0_00.y)
          .pos(1, struct.vert0_00.x + 6.0f, struct.vert0_00.y)
          .pos(2, struct.vert0_00.x, struct.vert0_00.y + 6.0f)
          .pos(3, struct.vert0_00.x + 6.0f, struct.vert0_00.y + 6.0f);

        if(s4 % 3 == 0) {
          //LAB_800f30d8
          cmd
            .uv(0, 176, 48)
            .uv(1, 183, 48)
            .uv(2, 176, 55)
            .uv(3, 183, 55);
        } else {
          cmd
            .uv(0, 184, 48)
            .uv(1, 191, 48)
            .uv(2, 184, 55)
            .uv(3, 191, 55);
        }

        GPU.queueCommand(41, cmd);
      }

      struct0.rotation_28 += MathHelper.psxDegToRad(this.savePointFloatiesRotations_800d6c88[fp]);
      struct0.rotation_28 %= MathHelper.TWO_PI;
    }
  }

  @Method(0x800f31bcL)
  private void handleTriangleIndicators() {
    this.triangleIndicator_800c69fc.screenOffsetX_10 = this.screenOffset_800cb568.x;
    this.triangleIndicator_800c69fc.screenOffsetY_14 = this.screenOffset_800cb568.y;

    if(gameState_800babc8.indicatorsDisabled_4e3) {
      return;
    }

    if(fullScreenEffect_800bb140.currentColour_28 != 0) {
      return;
    }

    final IndicatorMode indicatorMode = CONFIG.getConfig(CoreMod.INDICATOR_MODE_CONFIG.get());
    if(indicatorMode != IndicatorMode.MOMENTARY) {
      this.momentaryIndicatorTicks_800f9e9c = 0;
    }

    //LAB_800f321c
    if(Input.pressedThisFrame(InputAction.BUTTON_SHOULDER_RIGHT_1)) { // R1
      if(indicatorMode == IndicatorMode.OFF) {
        CONFIG.setConfig(CoreMod.INDICATOR_MODE_CONFIG.get(), IndicatorMode.MOMENTARY);
        //LAB_800f3244
      } else if(indicatorMode == IndicatorMode.MOMENTARY) {
        CONFIG.setConfig(CoreMod.INDICATOR_MODE_CONFIG.get(), IndicatorMode.ON);
      } else if(indicatorMode == IndicatorMode.ON) {
        CONFIG.setConfig(CoreMod.INDICATOR_MODE_CONFIG.get(), IndicatorMode.OFF);
        this.momentaryIndicatorTicks_800f9e9c = 0;
      }
      //LAB_800f3260
    } else if(Input.pressedThisFrame(InputAction.BUTTON_SHOULDER_LEFT_1)) { // L1
      if(indicatorMode == IndicatorMode.OFF) {
        //LAB_800f3274
        CONFIG.setConfig(CoreMod.INDICATOR_MODE_CONFIG.get(), IndicatorMode.ON);
        //LAB_800f3280
      } else if(indicatorMode == IndicatorMode.MOMENTARY) {
        CONFIG.setConfig(CoreMod.INDICATOR_MODE_CONFIG.get(), IndicatorMode.OFF);
        this.momentaryIndicatorTicks_800f9e9c = 0;
        //LAB_800f3294
      } else if(indicatorMode == IndicatorMode.ON) {
        CONFIG.setConfig(CoreMod.INDICATOR_MODE_CONFIG.get(), IndicatorMode.MOMENTARY);

        //LAB_800f32a4
        this.momentaryIndicatorTicks_800f9e9c = 0;
      }
    }

    //LAB_800f32a8
    //LAB_800f32ac
    if(CONFIG.getConfig(CoreMod.INDICATOR_MODE_CONFIG.get()) == IndicatorMode.OFF) {
      return;
    }

    final MV ls = new MV();
    GsGetLs(this.sobjs_800c6880[0].innerStruct_00.model_00.coord2_14, ls);

    PushMatrix();
    GTE.setTransforms(ls);

    GTE.perspectiveTransform(this.bottom_800d6cb8);
    final float bottomY = GTE.getScreenY(2);

    GTE.perspectiveTransform(this.top_800d6cc0);
    final float topY = GTE.getScreenY(2);

    GTE.perspectiveTransform(0, -(topY - bottomY) - 48, 0);
    this.triangleIndicator_800c69fc.playerX_08 = GTE.getScreenX(2);
    this.triangleIndicator_800c69fc.playerY_0c = GTE.getScreenY(2);

    PopMatrix();

    if(CONFIG.getConfig(CoreMod.INDICATOR_MODE_CONFIG.get()) == IndicatorMode.MOMENTARY) {
      if(this.momentaryIndicatorTicks_800f9e9c < 33) {
        this.renderTriangleIndicators();
        this.momentaryIndicatorTicks_800f9e9c++;
      }
      //LAB_800f3508
    } else if(CONFIG.getConfig(CoreMod.INDICATOR_MODE_CONFIG.get()) == IndicatorMode.ON) {
      this.renderTriangleIndicators();
    }

    //LAB_800f3518
  }

  @Method(0x800f352cL)
  private void renderTriangleIndicators() {
    final TriangleIndicator140 indicator = this.triangleIndicator_800c69fc;

    //LAB_800f35b0
    for(int indicatorIndex = 0; indicatorIndex < 21; indicatorIndex++) {
      final TriangleIndicator44 s1 = this._800d4ff0[indicatorIndex];

      final AnmFile anm;
      if(indicatorIndex == 0) {
        // Player indicator

        s1.x_34 = indicator.playerX_08;
        s1.y_38 = indicator.playerY_0c - 28;

        anm = this.playerIndicatorAnimation_800d5588.anm_00;
      } else {
        // Door indicators

        //LAB_800f35f4
        if(indicator._18[indicatorIndex - 1] < 0) {
          break;
        }

        s1.x_34 = indicator.screenOffsetX_10 - indicator.screenOffsetX_90[indicatorIndex - 1] + indicator.x_40[indicatorIndex - 1] -  2;
        s1.y_38 = indicator.screenOffsetY_14 - indicator.screenOffsetY_e0[indicatorIndex - 1] + indicator.y_68[indicatorIndex - 1] - 32;

        anm = this.doorIndicatorAnimation_800d5590.anm_00;
      }

      final AnmSpriteGroup[] spriteGroups = anm.spriteGroups;

      //LAB_800f365c
      if((s1._00 & 0x1) == 0) {
        if(((this.smapTicks_800c6ae0 % (3 - vsyncMode_8007a3b8) == 0))) {
          s1.time_08--;
        }

        if(s1.time_08 < 0) {
          s1.sequence_04++;

          if(s1.sequence_04 > s1.sequenceCount_14) {
            s1.sequence_04 = s1._10;
            s1._0c++;
          }

          //LAB_800f36b0
          s1.time_08 = anm.sequences_08[s1.sequence_04].time_02 - 1;
        }
      }

      //LAB_800f36d0
      final AnmSpriteGroup group = spriteGroups[anm.sequences_08[s1.sequence_04].spriteGroupNumber_00];
      final int count = group.n_sprite_00;

      //LAB_800f3724
      for(int s6 = count - 1; s6 >= 0; s6--) {
        final AnmSpriteMetrics14 sprite = group.metrics_04[s6];

        final float x = s1.x_34 - sprite.w_08 / 2.0f;
        final float y = s1.y_38;
        final int u = s1.u_1c + sprite.u_00;
        final int v = s1.v_20 + sprite.v_01;

        if(indicatorIndex == 0) { // Player indicator
          final int triangleIndex = this.getEncounterTriangleColour();
          this.mapIndicator.renderPlayerIndicator(GPU.getOffsetX() + x, GPU.getOffsetY() + y, 38, s1.r_24 / 128.0f, s1.g_25 / 128.0f, s1.b_26 / 128.0f, this._800d6cd8[triangleIndex] & 0x3f0, (sprite.cba_04 >>> 6 & 0x1ff) - this._800d6ce4[triangleIndex], u, v);
        } else { // Door indicators
          //LAB_800f3884
          this.mapIndicator.renderDoorIndicator(GPU.getOffsetX() + x, GPU.getOffsetY() + y, 38, s1.r_24 / 128.0f, s1.g_25 / 128.0f, s1.b_26 / 128.0f, 992, (sprite.cba_04 >>> 6 & 0x1ff) - this._800d6cc8[indicator._18[indicatorIndex - 1]], u, v);
        }

        //LAB_800f38b0
      }
    }

    //LAB_800f39d0
  }

  @Method(0x800f3a00L)
  private int getEncounterTriangleColour() {
    final int acc = this.encounterAccumulator_800c6ae8;

    if(acc <= 0xa00) {
      return 0;
    }

    //LAB_800f3a20
    if(acc <= 0xf00) {
      //LAB_800f3a40
      return 1;
    }

    //LAB_800f3a34
    return 2;
  }

  @Method(0x800f3a48L)
  private void initTriangleIndicators() {
    this.parseAnmFile(this.savepointAnm1, this.playerIndicatorAnimation_800d5588);
    this.parseAnmFile(this.savepointAnm2, this.doorIndicatorAnimation_800d5590);
    this.FUN_800f3b64(this.savepointAnm1, this.savepointAnm2, this._800d4ff0, 21);
  }

  @Method(0x800f3abcL)
  private void renderSubmapOverlays() {
    this.handleTriangleIndicators();

    if(this.hasSavePoint_800d5620) {
      this.renderSavePoint();
    }
  }

  @Method(0x800f3af8L)
  private void resetTriangleIndicators() {
    if(CONFIG.getConfig(CoreMod.INDICATOR_MODE_CONFIG.get()) != IndicatorMode.OFF) {
      this.momentaryIndicatorTicks_800f9e9c = 0;
    }

    //LAB_800f3b14
    final TriangleIndicator140 indicator = this.triangleIndicator_800c69fc;

    //LAB_800f3b24
    for(int i = 0; i < 20; i++) {
      indicator._18[i] = -1;
    }
  }

  @Method(0x800f3b64L)
  private void FUN_800f3b64(final AnmFile anm1, final AnmFile anm2, final TriangleIndicator44[] a2, final int count) {
    //LAB_800f3bc4
    for(int i = 0; i < count; i++) {
      final TriangleIndicator44 t2 = a2[i];

      final AnmFile anm;
      if(i == 0) {
        t2.tpage_18 = this.texPages_800d6050[0];
        t2.clut_1a = this.cluts_800d6068[0];
        t2.u_1c = this.dartArrowU_800d6ca8;
        t2.v_20 = this.dartArrowV_800d6cac;
        anm = anm1;
      } else {
        //LAB_800f3bfc
        t2.tpage_18 = this.texPages_800d6050[1];
        t2.clut_1a = this.cluts_800d6068[1];
        t2.u_1c = this.doorArrowU_800d6cb0;
        t2.v_20 = this.doorArrowV_800d6cb4;
        anm = anm2;
      }

      //LAB_800f3c24
      t2._00 = 0;
      t2.sequence_04 = 0;
      t2.time_08 = anm.sequences_08[0].time_02;
      t2._0c = 0;
      t2._10 = 0;
      t2.sequenceCount_14 = anm.n_sequence_06 - 1;

      t2.r_24 = 0x80;
      t2.g_25 = 0x80;
      t2.b_26 = 0x80;

      t2._28 = 0x1000;
      t2._2c = 0x1000;
      t2._30 = 0;
      t2.x_34 = 0.0f;
      t2.y_38 = 0.0f;
      t2._3c = 0;
    }
  }

  @Method(0x800f3b3cL)
  private void deallocateSavePoint() {
    this.hasSavePoint_800d5620 = false;
  }

  @Method(0x800f3c98L)
  private void parseAnmFile(final AnmFile anmFile, final AnimatedSprite08 a1) {
    a1.anm_00 = anmFile;
    a1.spriteGroup_04 = anmFile.spriteGroups;
  }

  @Method(0x800f4354L)
  private void handleAndRenderSubmapEffects() {
    if(this.unloadSubmapParticles_800c6870) {
      this.smokePlumeEffect_800d5fd8.deallocate();

      if(this.snowState_800f9e60 == 1 || this.snowState_800f9e60 == 2) {
        this.handleSnow();
      }

      this.smokeCloudEffect_800d4f50.deallocate();
    } else {
      this.smokePlumeEffect_800d5fd8.tickAndRenderSmokePlumeEffect(this.screenOffset_800cb568.x, this.screenOffset_800cb568.y);

      if(this.snowState_800f9e60 == 1) {
        this.handleSnow();
      }

      this.smokeCloudEffect_800d4f50.tickAndRenderSmokeCloudEffect(this.screenOffset_800cb568.x, this.screenOffset_800cb568.y);
    }
  }

  @Method(0x800f4420L)
  private void deallocateSmokeAndSnow() {
    this.smokePlumeEffect_800d5fd8.deallocate();

    if(this.snowState_800f9e60 == 1 || this.snowState_800f9e60 == 3) {
      this.handleSnow();
    }

    //LAB_800f4454
    this.smokeCloudEffect_800d4f50.deallocate();
  }

  /** Things such as the save point, &lt;!&gt; action icon, encounter icon, etc. */
  @Method(0x800f45f8L)
  private void reloadSubmapEffects() {
    if(this.submapEffectsState_800f9eac == -1) {
      //LAB_800f4714
      this.deallocateSavePoint();
      this.deallocateSmokeAndSnow();
      this.deallocateAttachedSobjEffects();
      this.submapEffectsLoadMode_800f9ea8 = 0;
      this.submapEffectsState_800f9eac = 0;
      return;
    }

    //LAB_800f4624
    final int loadMode = this.submapEffectsLoadMode_800f9ea8;
    if(loadMode == 0) {
      //LAB_800f4660
      this.loadMiscTextures(11);
      GPU.queueCommand(1, new GpuCommandCopyVramToVram(992, 288, 984, 288, 8, 64)); // Copies the save point texture beside itself
      this.submapEffectsLoadMode_800f9ea8++;
      this.submapEffectsState_800f9eac = 1;
    } else if(loadMode == 1) {
      //LAB_800f4650
      //LAB_800f46d8
      this.smokePlumeEffect_800d5fd8.effectShouldRender = false;
      this.initTriangleIndicators();
      this.initSavePoint();
      this.initAttachedSobjEffects();
      this.submapEffectsLoadMode_800f9ea8++;
      this.submapEffectsState_800f9eac = 2;
    }
    //LAB_800f473c
  }

  /**
   * Textures such as footsteps, encounter indicator, yellow &lt;!&gt; sign, save point, etc.
   */
  @Method(0x800f4754L)
  private void loadMiscTextures(final int textureCount) {
    //LAB_800f47f0
    for(int textureIndex = 0; textureIndex < textureCount; textureIndex++) {
      final Tim tim = new Tim(Unpacker.loadFile("SUBMAP/" + this.miscTextures_800f9eb0[textureIndex]));
      GPU.uploadData15(tim.getImageRect(), tim.getImageData());

      this.texPages_800d6050[textureIndex] = GetTPage(Bpp.values()[tim.getFlags() & 0b11], this.miscTextureTransModes_800d6cf0[textureIndex], tim.getImageRect().x, tim.getImageRect().y);
      this.cluts_800d6068[textureIndex] = tim.getClutRect().y << 6 | (tim.getClutRect().x & 0x3f0) >>> 4;

      GPU.uploadData15(tim.getClutRect(), tim.getClutData());
    }

    //LAB_800f48a8
  }
}
